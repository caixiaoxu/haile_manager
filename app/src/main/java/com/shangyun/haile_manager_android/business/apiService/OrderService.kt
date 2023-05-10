package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.OrderDetailEntity
import com.shangyun.haile_manager_android.data.entities.OrderListEntity
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
     * 订单详情接口
     */
    @GET("/order/details")
    suspend fun requestOrderDetail(@Query("orderId") orderId: Int): ResponseWrapper<OrderDetailEntity>
}