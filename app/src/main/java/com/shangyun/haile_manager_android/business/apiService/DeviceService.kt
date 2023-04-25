package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.DeviceEntity
import com.shangyun.haile_manager_android.data.entities.DeviceStatusTotal
import com.shangyun.haile_manager_android.data.entities.DeviceTypeOfImeiEntity
import com.shangyun.haile_manager_android.data.entities.SpuEntity
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

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

}