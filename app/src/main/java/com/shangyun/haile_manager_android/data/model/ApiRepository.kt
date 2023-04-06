package com.shangyun.haile_manager_android.data.model

import com.lsy.framelib.network.ApiService
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.BuildConfig
import com.shangyun.haile_manager_android.data.entities.LoginEntity

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
object ApiRepository {

    /**
     * 获取网络请求Retrofit
     * @param service api Service
     */
    fun <T> apiClient(service: Class<T>): T {
        return ApiService.get(BuildConfig.BASE_URL, service)
    }

    /**
     * 处理网络请求结果
     */
    fun <T> dealApiResult(response: ResponseWrapper<T>): T? {
        when (response.code) {
            100000 -> {// 未账号注册
                throw CommonCustomException(response.code, response.message)
            }
        }
        return response.data
    }
}