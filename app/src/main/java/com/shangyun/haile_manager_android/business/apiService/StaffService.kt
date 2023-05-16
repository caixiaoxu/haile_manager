package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    @POST("/merchant/createMember")
    suspend fun createStaff(@Body body: RequestBody): ResponseWrapper<Any>
}