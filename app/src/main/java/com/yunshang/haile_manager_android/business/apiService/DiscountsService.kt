package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
interface DiscountsService {
    /**
     * 激活补偿券
     */
    @POST("/coupon/subject/activateCompensate")
    suspend fun activateCompensate(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 请求优惠列表
     */
    @POST("/timeMarket/list")
    suspend fun requestDiscountList(@Body body: RequestBody): ResponseWrapper<ResponseList<DiscountsEntity>>

    /**
     * 请求优惠业务类型
     */
    @POST("/timeMarket/listBizType")
    suspend fun requestBusinessType(@Body body: RequestBody): ResponseWrapper<MutableList<DiscountsBusinessTypeEntity>>

    /**
     * 请求优惠设置类型
     */
    @POST("/timeMarket/listDeviceType")
    suspend fun requestDeviceCategory(@Body body: RequestBody): ResponseWrapper<MutableList<DiscountsDeviceTypeEntity>>

    /**
     * 保存优惠配置
     */
    @POST("/timeMarket/saveTimeMarket")
    suspend fun saveDiscountsConfig(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 删除优惠配置
     */
    @FormUrlEncoded
    @POST("/timeMarket/deleteTimeMarket")
    suspend fun deleteDiscountsConfig(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): ResponseWrapper<Any>

    /**
     * 优惠配置详情
     */
    @GET("/timeMarket/timeMarketDetail")
    suspend fun requestDiscountsDetail(@Query("id") id: Int): ResponseWrapper<DiscountsDetailEntity>

    /**
     * 发券
     */
    @POST("/coupon/subject/activate/app")
    suspend fun submitIssueCoupon(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 券列表
     */
    @POST("/coupon/assetSearch")
    suspend fun requestCouponList(@Body body: RequestBody): ResponseWrapper<ResponseList<CouponEntity>>

    /**
     * 券数量
     */
    @POST("/coupon/assetSearch/count")
    suspend fun requestCouponNum(@Body body: RequestBody): ResponseWrapper<MutableList<CouponNumEntity>>

}