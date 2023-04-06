package com.lsy.framelib.network

import com.lsy.framelib.network.gsonAdapters.GsonConverter
import com.lsy.framelib.network.intfs.IOkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 14:43
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ApiService {
    // 默认OkHttpClient
    private var mIClient: IOkHttpClient = DefaultOkHttpClient()

    // 存储Retrofit <BaseUrl,Retrofit>
    private val mRetrofitMap: MutableMap<String, Retrofit> = mutableMapOf()

    /**
     * 取出Retrofit
     * @param baseUrl url域名
     * @param service 请求地址service
     */
    fun <T> get(baseUrl: String, service: Class<T>): T {
        return this.getRetrofit<T>(baseUrl).create(service)
    }

    /**
     * 判断是否存储retrofit，没有就创建
     * @param baseUrl url域名
     */
    private fun <T> getRetrofit(baseUrl: String): Retrofit {
        //检测baseUrl
        if (baseUrl.isEmpty()) {
            throw IllegalArgumentException("baseUrl can not be empty")
        }
        // 判断是否已存在
        if (mRetrofitMap[baseUrl] != null) {
            return mRetrofitMap[baseUrl]!!
        }
        // 不存在，创建
        val mRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mIClient.getClient())
            .addConverterFactory(GsonConverterFactory.create(GsonConverter.createGson<T>()))
            .build()
        mRetrofitMap[baseUrl] = mRetrofit
        return mRetrofit
    }

    /**
     * 自定义OkHttpClient
     * @param client 实现IOkHttpClient接口的类
     */
    fun registerClient(client: IOkHttpClient) {
        this.mIClient = client
    }
}