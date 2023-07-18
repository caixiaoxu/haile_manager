package com.yunshang.haile_manager_android.web

import android.content.Intent
import android.graphics.Color
import android.net.http.SslError
import android.view.View
import android.webkit.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WebViewViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityWebviewBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.CustomCaptureActivity
import com.yunshang.haile_manager_android.ui.activity.login.LoginActivity
import com.yunshang.haile_manager_android.utils.DialogUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.web.bean.*


class WebViewActivity : BaseBusinessActivity<ActivityWebviewBinding, WebViewViewModel>(
    WebViewViewModel::class.java
) {
    private val jsInterfaceName = "WebViewJavascriptBridge"

    // 扫码相机启动器
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let { code ->
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
                            callResponse(JsResponseBean(JsScanResponseBean(scanRequest.type, code)))
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
        }
    }

    // 扫码配置
    private val scanOptions: ScanOptions by lazy {
        ScanOptions().apply {
            captureActivity = CustomCaptureActivity::class.java
            setPrompt("请对准一/二维码")//提示语
            setOrientationLocked(true)
            setCameraId(0) // 选择摄像头
            setBeepEnabled(true) // 开启声音
        }
    }

    override fun layoutId(): Int = R.layout.activity_webview

    override fun backBtn(): View = mBinding.barWebviewTitle.getBackBtn()

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
        mBinding.webview.settings.run {
            defaultTextEncodingName = "utf-8"
            builtInZoomControls = false
//            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            domStorageEnabled = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        mBinding.webview.webViewClient = object : WebViewClient() {
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

        mBinding.webview.webChromeClient = object : WebChromeClient() {

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
        }

        mBinding.webview.addJavascriptInterface(
            MainJavascriptInterface(mBinding.webview.callbacks)
            { type, json, callbackId -> callMethod(type, json, callbackId) }, jsInterfaceName
        )
        mBinding.webview.setGson(Gson())
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
                mBinding.webview.loadData(sbimgs.toString(), "text/html", "UTF-8")
            } else {
                mBinding.webview.loadUrl(url)
            }
        }
    }

    private fun callResponse(data: Any) {
        if (!mViewModel.callbackId.isNullOrEmpty()) {
            mBinding.webview.sendResponse(data, mViewModel.callbackId)
        }
        clearCache()
    }

    private fun clearCache() {
        mViewModel.jsScanRequestBean = null
        mViewModel.callbackId = null
    }

    override fun onBackListener() {
        if (mBinding.webview.canGoBack()) {
            mBinding.webview.goBack()
        } else {
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
                    scanLauncher.launch(scanOptions)
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