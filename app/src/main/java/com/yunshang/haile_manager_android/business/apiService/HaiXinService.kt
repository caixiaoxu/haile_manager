package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.HaiXinTotalEntity
import com.yunshang.haile_manager_android.data.entities.HaixinSchemeConfigEntity
import com.yunshang.haile_manager_android.data.entities.SchemeConfigsDetailEntity
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
     * 海星余额口
     */
    @POST("/starfish/getStarFishTradeAmountVO")
    suspend fun requestHaiXinTotal(@Body params: RequestBody): ResponseWrapper<HaiXinTotalEntity>

    /**
     * 充值方案列表接口
     */
    @POST("/starfish/shopConfig/getList")
    suspend fun requestSchemeList(@Body params: RequestBody): ResponseWrapper<ResponseList<HaixinSchemeConfigEntity>>

    /**
     * 充值方案列表接口
     */
    @POST("/starfish/shopConfig/get")
    suspend fun requestSchemeDetail(@Body params: RequestBody): ResponseWrapper<SchemeConfigsDetailEntity>

    /**
     * 开启方案接口
     */
    @POST("/starfish/shopConfig/open")
    suspend fun openSchemeConfigs(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 关闭方案接口
     */
    @POST("/starfish/shopConfig/close")
    suspend fun closeSchemeConfigs(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 删除方案接口
     */
    @POST("/starfish/shopConfig/delete")
    suspend fun deleteSchemeConfigs(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 请求退款二维码接口
     */
    @GET("/starfish/getRefundQrCode")
    suspend fun requestRefundQrCode(): ResponseWrapper<String>

    /**
     * 请求充值二维码接口
     */
    @GET("/starfish/getRechargeIdQrCode")
    suspend fun requestRechargeQrCode(@Query("shopId") shopId: Int): ResponseWrapper<String>

    /**
     * 创建充值方案接口
     */
    @POST("/starfish/shopConfig/create")
    suspend fun createSchemeConfig(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 修改充值方案接口
     */
    @POST("/starfish/shopConfig/update")
    suspend fun updateSchemeConfig(@Body params: RequestBody): ResponseWrapper<Any>
}