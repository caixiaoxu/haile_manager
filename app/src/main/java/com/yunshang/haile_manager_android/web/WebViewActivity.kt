package com.yunshang.haile_manager_android.web

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.google.gson.Gson
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WebViewViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityWebviewBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.login.LoginActivity
import com.yunshang.haile_manager_android.web.bean.JsCommonBean


class WebViewActivity : BaseBusinessActivity<ActivityWebviewBinding, WebViewViewModel>(
    WebViewViewModel::class.java
) {
    private val jsInterfaceName = "WebViewJavascriptBridge"

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
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            domStorageEnabled = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
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
            { type, json -> callMethod(type, json) }, jsInterfaceName
        )
        mBinding.webview.setGson(Gson())
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

    override fun onBackListener() {
        if (mBinding.webview.canGoBack()) {
            mBinding.webview.goBack()
        } else {
            super.onBackListener()
        }
    }

    private fun callMethod(type: Int, json: String) {
        if (null != mViewModel.whiteList.value) {
            val appId = GsonUtils.getValueOfKey<String>(json, "appId")
            if (appId.isNullOrEmpty()) return
        }
        when (type) {
            0 -> {
                // 关闭当前界面
                finish()
            }
            1 -> {
                // 退出登录
                GsonUtils.json2Class(
                    GsonUtils.getValueOfKey<String>(json, "data"),
                    JsCommonBean::class.java
                )?.let { bean ->
                    if (SPRepository.loginInfo?.userId.toString() == bean.uid) {
                        SPRepository.cleaLoginUserInfo()
                        AppManager.finishAllActivity()
                        startActivity(Intent(this@WebViewActivity, LoginActivity::class.java))
                    }
                }
            }
        }
    }
}