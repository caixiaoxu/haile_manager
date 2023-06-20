package com.yunshang.haile_manager_android.web

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.OnBridgeCallback
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/19 19:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MainJavascriptInterface(
    callbacks: Map<String?, OnBridgeCallback?>?,
    val callMethod: (type: Int, json: String, callbackId: String) -> Unit
) : BridgeWebView.BaseJavascriptInterface(callbacks) {
    private var jsCallbackId: String? = null

    override fun send(data: String): String {
        return "it is default response"
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    fun _HR_onPopGlobalWeb(data: String, callbackId: String) {
        Timber.i("js: ${data}, callbackId: $callbackId")
        callMethod(0, data, callbackId)
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    fun _HR_onExitApp(data: String, callbackId: String) {
        callMethod(1, data, callbackId)
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    fun _HR_getScan(data: String, callbackId: String) {
        callMethod(2, data, callbackId)
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    fun _HR_getUserInfo(data: String, callbackId: String) {
        callMethod(3, data, callbackId)
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    fun _HR_onUpdateNavTitle(data: String, callbackId: String) {
        callMethod(4, data, callbackId)
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    fun _HR_chooseImage(data: String, callbackId: String) {
        callMethod(5, data, callbackId)
    }
}