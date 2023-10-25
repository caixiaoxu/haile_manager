package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
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
     * spu列表接口 (新版 替换sku)
     */
    @POST("/spu/sku/v2")
    suspend fun skuV2(@Body params: RequestBody): ResponseWrapper<MutableList<SkuFunConfigurationV2Param>>

    /**
     * 标准功能列表(并集)列表接口
     */
    @POST("/sku/function/union/list")
    suspend fun unionSku(@Body params: RequestBody): ResponseWrapper<MutableList<SkuUnionIntersectionEntity>>

    /**
     * 标准功能列表(交集)列表接口
     */
    @POST("/sku/function/intersection/list")
    suspend fun intersectionSku(@Body params: RequestBody): ResponseWrapper<MutableList<SkuUnionIntersectionEntity>>

    /**
     * spu列表接口
     */
    @GET("/spu/list")
    suspend fun spuList(
        @Query("categoryId") categoryId: Int,
        @Query("shopIdList") shopIdList: IntArray? = null,
        @Query("positionIdList") positionIdList: IntArray? = null
    ): ResponseWrapper<MutableList<SpuEntity>>

    /**
     * spu列表（拆分子型号）接口
     */
    @GET("/spu/list/split")
    suspend fun spuListSplit(
        @Query("categoryId") categoryId: Int,
        @Query("shopIdList") shopIdList: IntArray? = null
    ): ResponseWrapper<MutableList<SpuEntity>>

    /**
     * spu详情接口
     */
    @GET("/spu/detail")
    suspend fun spuDetail(@Query("spuId") spuId: Int): ResponseWrapper<Spu>

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
     * 设备异常状态接口
     */
    @POST("/goods/getErrorDetailStatusTotalVo")
    suspend fun deviceErrorStatusTotals(@Body params: RequestBody): ResponseWrapper<DeviceErrorStatusTotalEntity>

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
     * 设备创建接口（新版，替换deviceCreate）
     */
    @POST("/goods/create/v2")
    suspend fun deviceCreateV2(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 设备编辑接口
     */
    @POST("/goods/edit")
    suspend fun deviceUpdate(@Body params: RequestBody): ResponseWrapper<DeviceDetailEntity>

    /**
     * 设备编辑接口（新版，替换deviceUpdate）
     */
    @POST("/goods/edit/v2")
    suspend fun deviceUpdateV2(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 设备删除接口
     */
    @POST("/goods/delete")
    suspend fun deviceDelete(@Body params: RequestBody): ResponseWrapper<Any>

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
    @FormUrlEncoded
    suspend fun deviceAdvancedSetting(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): ResponseWrapper<Any>

    /**
     * 设备复位接口
     */
    @POST("/device/reset")
    suspend fun deviceReset(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 设备启动接口
     */
    @POST("/device/start")
    suspend fun deviceStart(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 桶自洁接口
     */
    @POST("/device/clean")
    suspend fun deviceClean(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 开启或关闭设备预约接口
     */
    @POST("/goods/appointment/enable")
    suspend fun openOrCloseDeviceAppointment(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 批量修改接口
     */
    @POST("/goods/batch/edit")
    suspend fun batchEditDevice(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 批量启动接口
     */
    @POST("/goods/batch/start")
    suspend fun batchStartDevice(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 投放器设备设置
     */
    @POST("/dosing/passSetting")
    suspend fun devicePassSetting(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 投放器开盖
     */
    @POST("/dosing/openCap/start")
    suspend fun deviceOpenCap(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 投放器开盖
     */
    @POST("/dosing/volumeSetting")
    suspend fun deviceVolumeSetting(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 投放器温度
     */
    @POST("/dosing/temperatureSetting")
    suspend fun deviceTemperatureSetting(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 投放器核销洗衣液
     */
    @POST("/dosing/activate")
    suspend fun deviceActivate(@Body params: RequestBody): ResponseWrapper<Any>

    /**
     * 投放器开盖结束
     */
    @POST("/dosing/openCap/finish")
    suspend fun deviceOpenCapFinish(@Body params: RequestBody): ResponseWrapper<Any>

    @POST("/goods/scan")
    suspend fun requestGoodsScan(@Body params: RequestBody): ResponseWrapper<Int>
}