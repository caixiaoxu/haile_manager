package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.OrderDetailEntity
import com.yunshang.haile_manager_android.data.entities.OrderExecutiveLoggingEntity
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.data.entities.OrderSummaryCountEntity
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 15:11
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface OrderService {

    /**
     * 订单列表接口
     */
    @GET("/order/list")
    suspend fun requestOrderList(@QueryMap params: HashMap<String, @JvmSuppressWildcards Any>): ResponseWrapper<ResponseList<OrderListEntity>>

    /**
     * 订单特殊筛选数量接口
     */
    @GET("/order/summaryCount")
    suspend fun requestSummaryCount(@QueryMap params: HashMap<String, @JvmSuppressWildcards Any>): ResponseWrapper<OrderSummaryCountEntity>

    /**
     * 订单详情接口
     */
    @GET("/order/details")
    suspend fun requestOrderDetail(@Query("orderId") orderId: Int): ResponseWrapper<OrderDetailEntity>

    /**
     * 订单详情接口(根据订单号)
     */
    @GET("/order/details/byNo")
    suspend fun requestOrderDetailByNo(@Query("orderNo") orderNo: String): ResponseWrapper<OrderDetailEntity>

    /**
     * 订单详情接口
     */
    @POST("/order/refund")
    suspend fun requestOrderRefund(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 订单详情接口
     */
    @POST("/device/startByOrder")
    suspend fun requestOrderStart(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 订单复位接口
     */
    @POST("/device/resetByOrder")
    suspend fun requestOrderRestart(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 取消预约订单接口
     */
    @POST("/appoint/cancelOrder")
    suspend fun cancelAppointmentOrder(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 订单历史列表接口
     */
    @GET("/order/run/history")
    suspend fun requestOrderExecutiveLoggingList(@Query("orderId") orderId: Int): ResponseWrapper<MutableList<OrderExecutiveLoggingEntity>>

}