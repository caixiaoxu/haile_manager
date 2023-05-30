package com.lsy.framelib.network.interceptors

import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.network.intfs.IInterceptor
import okhttp3.HttpUrl
import okhttp3.Interceptor
import timber.log.Timber

/**
 * Title : 请求注入参数
 * Author: Lsy
 * Date: 2023/3/17 16:12
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BasicParamsInterceptor(
    private val dealHeaders: (() -> MutableMap<String, String>)? = null,
    private val dealParameters: ((params: Map<String, String>) -> Unit)? = null
) :
    IInterceptor {
    override fun getInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val originalHttpUrl = originalRequest.url()
            // 添加公共参数
            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameters(defaultBaseParameters(originalHttpUrl).apply {
                    dealParameters?.invoke(this)
                })
                .build()

            // 重新加入请求
            val newRequest = originalRequest.newBuilder().apply {
                url(newUrl)
                method(originalRequest.method(), originalRequest.body())

                dealHeaders?.let {
                    it().entries.forEach { entry ->
                        Timber.i("Http-Headers=====${entry.key}:${entry.value}")
                        addHeader(entry.key, entry.value)
                    }
                }
            }.build()
            chain.proceed(newRequest)
        }
    }

    /**
     * 添加请求参数
     * @param mQueryParameters 新增的请求参数
     */
    private fun HttpUrl.Builder.addQueryParameters(mQueryParameters: MutableMap<String, String>): HttpUrl.Builder {
        if (mQueryParameters.isNotEmpty()) {
            mQueryParameters.forEach {
                this.addQueryParameter(it.key, it.value)
            }
        }
        return this
    }

    /**
     * 封装默认的公共参数
     */
    private fun defaultBaseParameters(originalHttpUrl: HttpUrl): MutableMap<String, String> {
        return mutableMapOf(
            "platform" to Constants.API_PLATFORM,
        )
    }
}