package com.shangyun.haile_manager_android.data.model

import android.os.Handler
import android.os.Looper
import com.lsy.framelib.network.ApiService
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BuildConfig
import com.shangyun.haile_manager_android.utils.ActivityManagerUtils
import okhttp3.MediaType
import okhttp3.RequestBody

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
    private val mHandler = Handler(Looper.getMainLooper())

    /**
     * 获取网络请求Retrofit
     * @param service api Service
     */
    fun <T> apiClient(service: Class<T>): T {
        return ApiService.get(BuildConfig.BASE_URL, service)
    }


    /**
     * 生成请求body
     */
    fun createRequestBody(params: Map<String, Any>): RequestBody =
        createRequestBody(GsonUtils.any2Json(params))


    /**
     * 生成请求body
     */
    fun createRequestBody(paramsJson: String): RequestBody =
        RequestBody.create(MediaType.parse("application/json"), paramsJson)

    /**
     * 处理网络请求结果
     */
    fun <T> dealApiResult(response: ResponseWrapper<T>): T? {
        if (0 != response.code) {
            // 1 参数不正常
            // 100000 未账号注册
            // 100002 登录失效
            // 100003 账号在其他地方登录

            when (response.code) {
                100002, 100003 -> {
                    mHandler.post {
                        ActivityManagerUtils.clearLoginInfoGoLogin()
                    }
                }
            }
            throw CommonCustomException(response.code, response.message)
        }
        return response.data
    }
}