package com.lsy.framelib.network

import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.network.intfs.IOkHttpClient
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * Title : OkHttp默认的构建类
 * Author: Lsy
 * Date: 2023/3/16 18:28
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DefaultOkHttpClient : IOkHttpClient {
    private var mConnectionTimeOut = Constants.CONNECTION_TIMEOUT
    private var mWriteTimeOut = Constants.CONNECTION_TIMEOUT
    private var mReadTimeOut = Constants.CONNECTION_TIMEOUT
    private var isRetryOnConnectionFailure = Constants.IS_RETRY_ON_CONNECTION_FAILURE
    private var mInterceptors: Array<Interceptor> = emptyArray()
    private var mNetworkInterceptors: Array<Interceptor> = emptyArray()

    /**
     * 对外返回Okhttp实例
     */
    override fun getClient(): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(mConnectionTimeOut, TimeUnit.SECONDS)//连接超时
            .writeTimeout(mWriteTimeOut, TimeUnit.SECONDS)//写入超时
            .readTimeout(mReadTimeOut, TimeUnit.SECONDS)//读取超时
            .retryOnConnectionFailure(isRetryOnConnectionFailure)//失败重试
            .apply {
                mInterceptors.forEach {// 自定义拦截器
                    addInterceptor(it)
                }
                mNetworkInterceptors.forEach {// 网络拦截器
                    addNetworkInterceptor(it)
                }
            }.build()

    /**
     * 设置连接超时
     * @param time 时间(秒)
     */
    fun setConnectionTimeOut(time: Long): DefaultOkHttpClient {
        this.mConnectionTimeOut = time
        return this
    }

    /**
     * 设置写入超时
     * @param time 时间(秒)
     */
    fun setWriteTimeOut(time: Long): DefaultOkHttpClient {
        this.mWriteTimeOut = time
        return this
    }

    /**
     * 设置读取超时
     * @param time 时间(秒)
     */
    fun setReaderTimeOut(time: Long): DefaultOkHttpClient {
        this.mReadTimeOut = time
        return this
    }

    /**
     * 设置请求失败后是否重试
     * @param isRetry 是否重试
     */
    fun isRetryOnConnectionFailure(isRetry: Boolean): DefaultOkHttpClient {
        this.isRetryOnConnectionFailure = isRetry
        return this
    }

    /**
     * 设置拦截器
     */
    fun setInterceptors(interceptors: Array<Interceptor>): DefaultOkHttpClient {
        this.mInterceptors = interceptors
        return this
    }

    /**
     * 设置网络拦截器
     */
    fun setNetworkInterceptors(interceptors: Array<Interceptor>): DefaultOkHttpClient {
        this.mNetworkInterceptors = interceptors
        return this
    }
}