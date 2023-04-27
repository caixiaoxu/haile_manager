package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Title : 店铺接口
 * Author: Lsy
 * Date: 2023/3/17 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface ShopService {

    /**
     * 店铺列表接口
     */
    @POST("/shop/shopList")
    suspend fun shopList(@Body body: RequestBody): ResponseWrapper<ResponseList<ShopEntity>>

    /**
     * 店铺详情接口
     */
    @POST("/shop/shopDetail")
    suspend fun shopDetail(@Body body: RequestBody): ResponseWrapper<ShopDetailEntity>

    /**
     * 店铺类型
     */
    @GET("/shop/getShopTypeList")
    suspend fun shopTypeList(): ResponseWrapper<List<ShopTypeEntity>>

    /**
     * 店铺业务类型
     */
    @GET("/shop/shopBusinessType")
    suspend fun shopBusinessType(): ResponseWrapper<List<ShopBusinessTypeEntity>>

    /**
     * 学校列表
     */
    @POST("/shop/getSchoolList")
    suspend fun schoolList(@Body body: RequestBody): ResponseWrapper<MutableList<SchoolSelectEntity>>

    /**
     * 店铺创建
     */
    @POST("/shop/createShop")
    suspend fun createShop(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 店铺修改
     */
    @POST("/shop/updateShop")
    suspend fun updateShop(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 店铺删除
     */
    @POST("/shop/deleteShop")
    suspend fun deleteShop(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 店铺选择列表
     */
    @POST("/shop/shopSelectList")
    suspend fun shopSelectList(@Body body: RequestBody): ResponseWrapper<MutableList<ShopSelectEntity>>

    /**
     * 店铺预约设置列表
     */
    @GET("/appoint/getSetting")
    suspend fun getShopAppointmentSettingList(@Query("shopId") shopId: Int): ResponseWrapper<MutableList<AppointmentSettingEntity>>

    /**
     * 店铺预约设置
     */
    @GET("/appoint/setting")
    suspend fun setShopAppointment(@Body body: RequestBody): ResponseWrapper<Any>
}