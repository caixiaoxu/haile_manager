package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.*

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
    @GET("/position/getShopPositionCountVO")
    suspend fun requestShopAndPositionNum(): ResponseWrapper<ShopAndPositionNumEntity>

    /**
     * 店铺列表接口
     */
    @POST("/shop/shopList")
    suspend fun shopList(@Body body: RequestBody): ResponseWrapper<ResponseList<ShopEntity>>

    /**
     * 带点位数量的店铺列表接口
     */
    @POST("/position/positionShopList")
    suspend fun requestShopList(@Body body: RequestBody): ResponseWrapper<ResponseList<ShopEntity>>

    /**
     * 店铺的点位列表接口
     */
    @GET("/position/positionListByShopId")
    suspend fun requestPositionList(@QueryMap params: HashMap<String, Any?>): ResponseWrapper<ResponseList<ShopPositionEntity>>

    /**
     * 店铺的点位设备数接口
     */
    @GET("/position/positionDeviceNum")
    suspend fun requestPositionDeviceNum(
        @Query("shopId") shopId: Int,
        @Query("positionId") positionId: Int
    ): ResponseWrapper<MutableList<PositionDeviceNumEntity>>

    /**
     * 店铺搜索列表接口
     */
    @POST("/shop/shopSearchList")
    suspend fun shopSearchList(@Body body: RequestBody): ResponseWrapper<MutableList<ShopSearchEntity>>

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
     * 数据统计店铺选择列表
     */
    @POST("/shop/shopDatePermissionSelectList")
    suspend fun requestStatisticsShopSelectList(@Body body: RequestBody): ResponseWrapper<MutableList<ShopSelectEntity>>

    /**
     * 海星方案店铺选择列表
     */
    @POST("/shop/shopSelectListForStarFish")
    suspend fun requestHaiXinSchemeShopSelectList(@Body body: RequestBody): ResponseWrapper<MutableList<ShopSelectEntity>>

    /**
     * 店铺预约设置列表
     */
    @GET("/appoint/getSetting")
    suspend fun getShopAppointmentSettingList(@Query("shopId") shopId: Int): ResponseWrapper<MutableList<AppointmentSettingEntity>>

    /**
     * 店铺预约设置
     */
    @POST("/appoint/setting")
    suspend fun setShopAppointment(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 店铺支付设置模版
     */
    @POST("/pay/settingsTemplate")
    suspend fun getShopPaySettingsTemplate(@Body body: RequestBody): ResponseWrapper<ShopPaySettingsEntity>

    /**
     * 绑定店铺支付设置
     */
    @POST("/pay/batchCreateSettings")
    suspend fun batchShopPaySettings(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 保存店铺运营设置
     */
    @POST("/shop/saveOperationSetting")
    suspend fun saveOperationSetting(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 创建点位
     */
    @POST("/position/addSubOrganizationPosition")
    suspend fun createPosition(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 修改点位
     */
    @POST("/position/updateSubOrganizationPosition")
    suspend fun updatePosition(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 修改点位状态
     */
    @POST("/position/updateState")
    suspend fun updatePositionState(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 点位详情
     */
    @GET("/position/getDetail")
    suspend fun requestPositionDetail(@Query("id") positionId: Int): ResponseWrapper<ShopPositionDetailEntity>

    /**
     * 点位删除
     */
    @POST("/position/deleteSubOrganizationPosition")
    suspend fun deletePosition(@Body body: RequestBody): ResponseWrapper<Any>
}