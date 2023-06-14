package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.HaiXinTotalEntity
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface HaiXinService {

    /**
     * 设备详情接口
     */
    @POST("/starfish/getStarFishTradeAmountVO")
    suspend fun requestHaiXinTotal(@Body params: RequestBody): ResponseWrapper<HaiXinTotalEntity>

    /**
     * 请求退款二维码接口
     */
    @GET("/starfish/getRefundQrCode")
    suspend fun requestRefundQrCode(): ResponseWrapper<String>

}