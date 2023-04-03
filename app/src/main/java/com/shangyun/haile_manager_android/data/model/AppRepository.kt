package com.shangyun.haile_manager_android.data.model

import com.lsy.framelib.network.ApiService
import com.shangyun.haile_manager_android.BuildConfig

/**
 * Title : Repository层，获取数据来源
 * Author: Lsy
 * Date: 2023/3/17 17:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object AppRepository {

    /**
     * 获取网络请求Retrofit
     * @param service api Service
     */
    fun <T> apiClient(service: Class<T>): T {
        return ApiService.get(BuildConfig.BASE_URL, service)
    }

    /**
     * 获取网络请求Retrofit
     * @param baseUrl
     * @param service api Service
     */
    fun <T> apiClient(baseUrl: String, service: Class<T>): T {
        return ApiService.get(BuildConfig.BASE_URL, service)
    }
}