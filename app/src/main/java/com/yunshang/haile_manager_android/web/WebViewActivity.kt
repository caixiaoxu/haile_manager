package com.yunshang.haile_manager_android.web

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.view.View
import android.webkit.*
import androidx.activity.result.contract.ActivityResultContracts
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.WebViewViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityWebviewBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.ui.activity.login.LoginActivity
import com.yunshang.haile_manager_android.utils.DialogUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.web.bean.*
import timber.log.Timber


class WebViewActivity : BaseBusinessActivity<ActivityWebviewBinding, WebViewViewModel>(
    WebViewViewModel::class.java
) {
    private var mWebView: BridgeWebView? = null
    private val jsInterfaceName = "WebViewJavascriptBridge"
    private var fileChooserParams: WebChromeClient.FileChooserParams? = null
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    // 权限
    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
            if (result.values.any { it }) {
                // 授权权限成功
                fileChooserParams?.createIntent()?.let { intent ->
                    startFileChooser.launch(intent)
                    fileChooserParams = null
                } ?: run {
                    startQRActivity(false)
                }
            } else {
                // 授权失败
                SToast.showToast(this, R.string.empty_permission)
            }
        }

    // 文件选择界面
    private val startFileChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            fileCallback?.let { callback ->
                callback.onReceiveValue(if (result.resultCode == RESULT_OK && null != result.data) {
                    result.data?.dataString?.let { arrayOf(Uri.parse(it)) }
                } else null)
                fileCallback = null
            }
        }

    private fun startQRActivity(isOne: Boolean) {
        startQRCodeScan.launch(Intent(
            this,
            WeChatQRCodeScanActivity::class.java
        ).apply {
            putExtra("isOne", isOne)
        })
    }

    // 扫码相机启动器
    private val startQRCodeScan =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 扫码结果
            if (result.resultCode == RESULT_OK) {
                CameraScan.parseScanResult(result.data)?.let { code ->
                    Timber.i("扫码:$code")
                    mViewModel.jsScanRequestBean?.let { scanRequest ->
                        when (scanRequest.typeVal) {
                            -1 -> callResponse(
                                JsResponseBean(JsScanResponseBean(scanRequest.type, code))
                            )
                            0 -> if (15 == code.trim().length) {
                                callResponse(
                                    JsResponseBean(JsScanResponseBean(
                                        scanRequest.type, code, scanRequest.getTypeStr(1)
                                    ).apply {
                                        imei = code
                                    })
                                )
                            } else {
                                val payCode: String? = StringUtils.getPayCode(code)
                                if (payCode.isNullOrEmpty()) {
                                    callResponse(
                                        JsResponseBean(
                                            JsScanResponseBean(
                                                scanRequest.type,
                                                code
                                            )
                                        )
                                    )
                                } else {
                                    callResponse(
                                        JsResponseBean(
                                            JsScanResponseBean(
                                                scanRequest.type, code, scanRequest.getTypeStr(2),
                                            ).apply {
                                                pay = code
                                            }
                                        )
                                    )
                                }
                            }
                            1 -> if (15 == code.trim().length) {
                                callResponse(
                                    JsResponseBean(JsScanResponseBean(
                                        scanRequest.type, code,
                                        scanRequest.getTypeStr(1)
                                    ).apply {
                                        imei = code
                                    })
                                )
                            } else {
                                callResponse(JsResponseBean<Any>(4, "IMEI码不正确"))
                            }
                            2 -> {
                                val payCode: String? = StringUtils.getPayCode(code)
                                if (payCode.isNullOrEmpty()) {
                                    callResponse(JsResponseBean<Any>(4, "付款码不正确"))
                                } else {
                                    callResponse(
                                        JsResponseBean(JsScanResponseBean(
                                            scanRequest.type, code,
                                            scanRequest.getTypeStr(2)
                                        ).apply {
                                            pay = code
                                        })
                                    )
                                }
                            }
                        }
                    }
                } ?: SToast.showToast(this, R.string.scan_code_error)
            }
        }

    override fun layoutId(): Int = R.layout.activity_webview

    override fun backBtn(): View = mBinding.barWebviewTitle.getBackBtn()

    override fun onResume() {
        super.onResume()
        LiveDataBus.with(BusEvents.SCAN_CHANGE_STATUS, Boolean::class.java)?.observe(this) {
            startQRActivity(it)
        }
    }

    override fun onPause() {
        super.onPause()
        LiveDataBus.remove(BusEvents.SCAN_CHANGE_STATUS)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.whiteList.observe(this) {
            loadUrl()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        IntentParams.WebViewParams.parseTitle(intent)?.let {
            mBinding.barWebviewTitle.setTitle(it)
        }

        initWebView()
    }

    private fun initWebView() {
        mWebView = BridgeWebView(applicationContext).apply {
            settings.run {
                defaultTextEncodingName = "utf-8"
                builtInZoomControls = false
//            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                domStorageEnabled = true
                allowFileAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                if (IntentParams.WebViewParams.parseNoCache(intent)) {
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }
            }

            webViewClient = object : WebViewClient() {
                //防止加载网页时调起系统浏览器
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
//                super.onReceivedSslError(view, handler, error)
                }
            }

            webChromeClient = object : WebChromeClient() {

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)

                    title?.let {
                        if (IntentParams.WebViewParams.parseAutoWebTitle(intent)) {
                            mBinding.barWebviewTitle.setTitle(it)
                        }
                    }
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        hideLoading()
                    } else {
                        showLoading()
                    }
                    super.onProgressChanged(view, newProgress)
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@WebViewActivity.fileChooserParams = fileChooserParams
                    fileCallback = filePathCallback
                    requestMultiplePermission.launch(SystemPermissionHelper.readWritePermissions())
                    return true
                }
            }

            addJavascriptInterface(
                MainJavascriptInterface(callbacks)
                { type, json, callbackId -> callMethod(type, json, callbackId) }, jsInterfaceName
            )
            setGson(Gson())
        }
        mBinding.flWebview.addView(mWebView)
    }

    private fun changeTitle(bean: JsTitleRequestBean) {
        if (bean.title.isNotEmpty()) {
            mBinding.barWebviewTitle.setTitle(bean.title)
        }

        if (bean.font > 0) {
            mBinding.barWebviewTitle.getTitle().textSize = bean.font.toFloat()
        }

        try {
            if (bean.color.isNotEmpty()) {
                mBinding.barWebviewTitle.getTitle().setTextColor(Color.parseColor(bean.color))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (bean.bgColor.isNotEmpty()) {
                mBinding.barWebviewTitle.getTitle()
                    .setBackgroundColor(Color.parseColor(bean.bgColor))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initData() {
        if (IntentParams.WebViewParams.parseNeedFilter(intent)) {
            mViewModel.requestWhiteList()
        } else loadUrl()
    }

    private fun loadUrl() {
        IntentParams.WebViewParams.parseUrl(intent)?.let { url ->
            if (url.lastIndexOf(".jpg") != -1 || url.lastIndexOf(".png") != -1) {// 加载图片
                val sbimgs = StringBuilder("<html><center>")
                sbimgs.append("<img src=$url width=\"100%\">")
                sbimgs.append("</br>")
                sbimgs.append("</center></html>")
                mWebView?.loadData(sbimgs.toString(), "text/html", "UTF-8")
            } else {
                mWebView?.loadUrl(url)
            }
        }
    }

    private fun callResponse(data: Any) {
        if (!mViewModel.callbackId.isNullOrEmpty()) {
            mWebView?.sendResponse(data, mViewModel.callbackId)
        }
        clearCache()
    }

    private fun clearCache() {
        mViewModel.jsScanRequestBean = null
        mViewModel.callbackId = null
    }

    override fun onBackListener() {
        if (mWebView?.canGoBack() == true) {
            mWebView?.goBack()
        } else {
            // 清空缓存
            mWebView?.clearCache(true)
            mWebView?.clearFormData()
            mWebView?.clearHistory()
            // 销毁控件
            mBinding.flWebview.removeView(mWebView)
            mWebView?.destroy()
            mWebView = null
            super.onBackListener()
        }
    }

    private fun callMethod(type: Int, json: String, callbackId: String) {
        if (null != mViewModel.whiteList.value) {
            val appId = GsonUtils.getValueOfKey<String>(json, "appId")
            if (appId.isNullOrEmpty()) return
        }
        mViewModel.callbackId = callbackId
        when (type) {
            0 -> {
                // 关闭当前界面
                finish()
            }
            1 -> {
                // 退出登录
                GsonUtils.json2ClassType<JsRequestBean<JsCommonBean>>(
                    json,
                    object : TypeToken<JsRequestBean<JsCommonBean>>() {}.type
                )?.let { bean ->
                    if (SPRepository.loginInfo?.userId.toString() == bean.data.uid) {
                        SPRepository.cleaLoginUserInfo()
                        AppManager.finishAllActivity()
                        startActivity(Intent(this@WebViewActivity, LoginActivity::class.java))
                    } else callResponse(JsResponseBean<Any?>(4, "uid不是当前用户"))
                } ?: callResponse(JsResponseBean<Any?>(-1, "参数不正确"))
            }
            2 -> {
                GsonUtils.json2ClassType<JsRequestBean<JsScanRequestBean>>(
                    json,
                    object : TypeToken<JsRequestBean<JsScanRequestBean>>() {}.type
                )?.let { bean ->
                    mViewModel.jsScanRequestBean = bean.data
                    requestMultiplePermission.launch(
                        SystemPermissionHelper.cameraPermissions()
                            .plus(SystemPermissionHelper.readWritePermissions())
                    )
                } ?: callResponse(JsResponseBean<Any?>(-1, "参数不正确"))
            }
            3 -> {
                if (null != SPRepository.loginInfo && null != SPRepository.userInfo) {
                    callResponse(
                        JsResponseBean(
                            JsUserResponseBean(
                                SPRepository.loginInfo!!.token,
                                SPRepository.loginInfo!!.userId,
                                SPRepository.userInfo!!.userInfo,
                                SPRepository.userInfo!!.organization,
                            )
                        )
                    )
                } else {
                    callResponse(JsResponseBean<Any?>(4, "无法获取到用户信息"))
                }
            }
            4 -> {
                GsonUtils.json2ClassType<JsRequestBean<JsTitleRequestBean>>(
                    json, object : TypeToken<JsRequestBean<JsTitleRequestBean>>() {}.type
                )?.let { bean ->
                    runOnUiThread {
                        changeTitle(bean.data)
                    }
                } ?: callResponse(JsResponseBean<Any?>(-1, "参数不正确"))
            }
            5 -> {
                GsonUtils.json2ClassType<JsRequestBean<JsChooseImageBean>>(
                    json, object : TypeToken<JsRequestBean<JsChooseImageBean>>() {}.type
                )?.let { bean ->
                    runOnUiThread {
                        DialogUtils.showImgSelectorDialog(
                            this@WebViewActivity,
                            bean.data.count,
                            bean.data.sourceType.contains("camera"),
                            bean.data.sourceType.contains("album")
                        ) { isSuccess, result ->
                            if (isSuccess && !result.isNullOrEmpty()) {
                                mViewModel.uploadImg(result[0].cutPath) { isSuccess, url ->
                                    if (isSuccess && !url.isNullOrEmpty()) {
                                        callResponse(JsResponseBean(url))
                                    } else {
                                        callResponse(JsResponseBean<Any?>(4, "图片上传失败"))
                                    }
                                }
                            } else callResponse(JsResponseBean<Any?>(4, "图片获取失败"))
                        }
                    }
                } ?: callResponse(JsResponseBean<Any?>(-1, "参数不正确"))
            }
        }
    }
}