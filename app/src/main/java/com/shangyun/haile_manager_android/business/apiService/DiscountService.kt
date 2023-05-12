package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/11 19:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface DiscountService {

    @POST("/coupon/subject/activateCompensate")
    suspend fun activateCompensate(@Body body: RequestBody): ResponseWrapper<Any>
}