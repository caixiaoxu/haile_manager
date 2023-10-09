package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.StaffDetailEntity
import com.yunshang.haile_manager_android.data.entities.StaffEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/16 14:04
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface StaffService {

    /**
     * 人员列表接口
     */
    @POST("/merchant/memberList")
    suspend fun requestStaffList(@Body body: RequestBody): ResponseWrapper<ResponseList<StaffEntity>>

    /**
     * 人员角色接口
     */
    @GET("/merchant/userRoleList")
    suspend fun requestRoleList(): ResponseWrapper<List<String>>

    /**
     * 新增人员
     */
    @POST("/merchant/createMember/v2")
    suspend fun createStaff(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 人员详情
     */
    @GET("/merchant/memberDetail")
    suspend fun requestStaffDetail(@Query("userId") userId: Int): ResponseWrapper<StaffDetailEntity>

    /**
     * 人员删除
     */
    @POST("/merchant/logout")
    suspend fun deleteStaff(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 修改人员权限
     */
    @POST("/merchant/updateMemberMenuV2")
    suspend fun updateStaffPermission(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 修改人员负责的门店
     */
    @POST("/merchant/userManageOrganization")
    suspend fun updateStaffTakeChargeShop(@Body body: RequestBody): ResponseWrapper<Any>
}