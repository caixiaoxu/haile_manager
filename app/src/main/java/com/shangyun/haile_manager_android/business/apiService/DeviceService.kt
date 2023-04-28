package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Title : 设备接口
 * Author: Lsy
 * Date: 2023/4/23 10:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface DeviceService {

    /**
     * spu列表接口
     */
    @GET("/spu/{spuId}/sku")
    suspend fun sku(@Path("spuId") spuId: Int): ResponseWrapper<MutableList<SkuEntity>>

    /**
     * spu列表接口
     */
    @GET("/spu/list")
    suspend fun spuList(@Query("categoryId") categoryId: Int): ResponseWrapper<MutableList<SpuEntity>>

    /**
     * spu列表接口
     */
    @GET("/spu/deviceType")
    suspend fun deviceTypeOfImei(@Query("imei") imei: String): ResponseWrapper<DeviceTypeOfImeiEntity>

    /**
     * 设备状态接口
     */
    @GET("/goods/statusTotals")
    suspend fun deviceStatusTotals(@QueryMap params: HashMap<String, Any>): ResponseWrapper<DeviceStatusTotal>

    /**
     * 设备列表接口
     */
    @GET("/goods/list")
    suspend fun deviceList(@QueryMap params: HashMap<String, Any>): ResponseWrapper<ResponseList<DeviceEntity>>

    /**
     * 设备创建接口
     */
    @POST("/goods/create")
    suspend fun deviceCreate(@Body params: RequestBody): ResponseWrapper<DeviceDetailEntity>

    /**
     * 设备编辑接口
     */
    @POST("/goods/edit")
    suspend fun deviceUpdate(@Body params: RequestBody): ResponseWrapper<DeviceDetailEntity>

    /**
     * 设备详情接口
     */
    @GET("/goods/details")
    suspend fun deviceDetail(@Query("goodsId") goodsId: Int): ResponseWrapper<DeviceDetailEntity>

    /**
     * 高级设置接口
     */
    @GET("/device/advanced/values")
    suspend fun deviceAdvancedValues(@Query("goodsId") goodsId: Int): ResponseWrapper<MutableList<DeviceAdvancedSettingEntity>>

    /**
     * 设置高级设置接口
     */
    @POST("/device/advanced/setting")
    suspend fun deviceAdvancedSetting(@Body params: RequestBody): ResponseWrapper<Any>
}