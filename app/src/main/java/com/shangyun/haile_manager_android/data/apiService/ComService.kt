package com.shangyun.haile_manager_android.data.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.LoginEntity
import com.shangyun.haile_manager_android.data.entities.UserInfoEntity
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity
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
interface CommService {

    /**
     * 登录接口
     */
    @POST("/login/login")
    suspend fun login(@Body body: RequestBody): ResponseWrapper<LoginEntity>

    /**
     * 用户信息接口
     */
    @POST("/user/userInfo")
    suspend fun userInfo(): ResponseWrapper<UserInfoEntity>

    /**
     * 用户权限接口
     */
    @POST("/permission/getMenuDetailListByUser")
    suspend fun permissionByUser(): ResponseWrapper<List<UserPermissionEntity>>


}