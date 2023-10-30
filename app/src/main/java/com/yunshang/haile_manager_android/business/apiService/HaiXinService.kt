package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
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
     * 批量设置充值方案接口
     */
    @POST("/starfish/shopConfig/create/batch")
    suspend fun batchSettingSchemeConfig(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 修改充值方案接口
     */
    @POST("/starfish/shopConfig/update")
    suspend fun updateSchemeConfig(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 充值明细接口
     */
    @POST("/starfish/getStarFishTradeListVOList")
    suspend fun requestHaiXinRechargeList(@Body params: RequestBody): ResponseWrapper<ResponseList<HaixinRechargeEntity>>

    /**
     * 充值详情接口
     */
    @GET("/starfish/getStarFishLogVO")
    suspend fun rechargeDetail(
        @Query("orderNo") orderNo: String? = null,
        @Query("id") id: Int? = null
    ): ResponseWrapper<RechargeDetailEntity>

    /**
     * 充值订单详情接口
     */
    @GET("/starfish/getStarFishTradeVO")
    suspend fun rechargeOrderDetail(
        @Query("orderNo") orderNo: String? = null,
        @Query("id") id: Int? = null
    ): ResponseWrapper<RechargeOrderDetailEntity>

    /**
     * 充值用户列表接口
     */
    @POST("/starfish/getUserStarFishListVO")
    suspend fun requestRechargeAccountsList(@Body params: RequestBody): ResponseWrapper<ResponseList<HaixinRechargeAccountEntity>>

    /**
     * 充值用户详情每月总计接口
     */
    @POST("/starfish/getUserStarFishDetailVO")
    suspend fun requestRechargeAccountDetailTotalOfMonth(@Body params: RequestBody): ResponseWrapper<HaixinRechargeAccountEntity>

    /**
     * 充值用户详情列表接口
     */
    @POST("/starfish/getStarFishLogVOPageByUserIdAndShopId")
    suspend fun requestRechargeAccountDetailList(@Body params: RequestBody): ResponseWrapper<ResponseList<HaixinRechargeAccountDetailEntity>>

    /**
     * 充值回收接口
     */
    @POST("/starfish/expenditure")
    suspend fun rechargeRecycle(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 充值接口
     */
    @POST("/starfish/addStarFish")
    suspend fun rechargeHaiXin(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 退款记录列表接口
     */
    @POST("/starfish/getUserTokenCoinRefundRecordAppListVOPage")
    suspend fun requestRefundRecordList(@Body params: RequestBody): ResponseWrapper<ResponseList<RefundRecordEntity>>

    /**
     * 退款记录详情接口
     */
    @GET("/starfish/getUserTokenCoinRefundRecordDetailVOById")
    suspend fun requestRefundRecordDetail(@Query("id") id: Int): ResponseWrapper<RefundRecordDetailEntity>

    /**
     * 拒绝退款接口
     */
    @POST("/starfish/refundReject")
    suspend fun refuseRefund(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 同意退款接口
     */
    @POST("/starfish/refundPass")
    suspend fun agreeRefund(@Body params: RequestBody): ResponseWrapper<Any>

}