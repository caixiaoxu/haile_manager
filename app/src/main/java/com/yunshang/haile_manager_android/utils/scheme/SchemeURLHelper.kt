package com.yunshang.haile_manager_android.utils.scheme

import android.content.Context
import android.content.Intent
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.web.WebViewActivity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/13 17:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object SchemeURLHelper {
    private val PathMap = mapOf<String, ISchemeURLParser>(
        "DeviceRepairsReply" to DeviceRepairsReplyParser(),
        "DeviceUnbindApproveDetails" to DeviceUnbindApproveParser()
    )

    /**
     * 解析scheme url
     * @param context
     * @param link 广告位链接
     */
    fun parseSchemeURL(context: Context, link: String) {
        when (parseScheme(link)) {
            1 -> {
                Intent(
                    context,
                    WebViewActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.WebViewParams.pack(link)
                    )
                }
            }

            else -> formatSchemeURLToIntent(context, link)?.let { context.startActivity(it) }
        }
    }

    private fun formatSchemeURLToIntent(context: Context, link: String): Intent? {
        return parsePath(context, link) { packageName, path, params ->
            PathMap[path]?.let { parser ->
                Intent().apply {
                    setClassName(packageName, parser.parsePath())
                    params?.let {
                        parser.parseParams(params)?.let {
                            putExtras(it)
                        }
                    }
                }
            }
        }
    }

    /**
     * 解析scheme
     * @param link
     * https://www.baidu.com
     * android://com.yunshang.haile_life/NearByShop?isRechargeShop=true
     */
    private fun parseScheme(link: String): Int {
        val schemeEnd = link.indexOf("://")
        if (-1 == schemeEnd) return 0
        return when (link.substring(0, schemeEnd)) {
            "http", "https" -> 1
//            "mini" -> 2
            else -> 0
        }
    }

    /**
     * 解析Path和参数
     */
    private fun parsePath(
        context: Context,
        link: String,
        host: String? = null,
        callback: (packageName: String, path: String?, params: String?) -> Intent?
    ): Intent? {
        val packageName =
            host ?: context.packageManager.getPackageInfo(context.packageName, 0).packageName
        val packageNameIndex = link.indexOf(packageName)
        if (-1 != packageNameIndex) {
            val pathStart = packageNameIndex + packageName.length + 1
            val isEnd = link.indexOf("?", pathStart)
            return if (-1 == isEnd) {
                callback(packageName, link.substring(pathStart), null)
            } else {
                callback(packageName, link.substring(pathStart, isEnd), link.substring(isEnd + 1))
            }
        }
        return null
    }

    /**
     * 解析出单个参数的值
     */
    fun parseParam(params: String, paramName: String, start: Int = 0): String? {
        val paramNameLength = paramName.length
        val paramIndex = params.indexOf(paramName)
        // 判断参数是否存在
        if (-1 != paramIndex) {
            val paramValueStart = paramIndex + paramNameLength + 1
            val isEnd = params.indexOf("&", paramValueStart)
            return if (-1 == isEnd) params.substring(paramValueStart)
            else params.substring(paramValueStart, isEnd)
        }
        return null
    }
}