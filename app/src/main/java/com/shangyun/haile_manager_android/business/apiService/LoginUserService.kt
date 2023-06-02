package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.LoginEntity
import com.shangyun.haile_manager_android.data.entities.UserInfoEntity
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Title : 登录和用户信息接口
 * Author: Lsy
 * Date: 2023/3/17 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface LoginUserService {

    /**
     * 登录接口
     */
    @POST("/login/login")
    suspend fun login(@Body body: RequestBody): ResponseWrapper<LoginEntity>

    /**
     * 验证码接口
     */
    @POST("/login/getCode")
    suspend fun sendCode(@Body body: RequestBody): ResponseWrapper<String>

    /**
     * 忘记密码接口
     */
    @POST("/login/updatePasswordByCode")
    suspend fun forgetPassword(@Body body: RequestBody): ResponseWrapper<Boolean>

    /**
     * 修改密码接口
     */
    @POST("/user/updatePassword")
    suspend fun updatePassword(@Body body: RequestBody): ResponseWrapper<Boolean>

    /**
     * 用户信息接口
     */
    @POST("/user/userInfo")
    suspend fun userInfo(): ResponseWrapper<UserInfoEntity>

    /**
     * 检验Token是否有效接口
     */
    @POST("/login/checkToken")
    suspend fun checkToken(@Body body: RequestBody): ResponseWrapper<LoginEntity>

    /**
     * 登出接口
     */
    @POST("/login/logout")
    suspend fun logout(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 用户权限接口
     */
    @POST("/permission/getMenuDetailListByUser")
    suspend fun permissionByUser(): ResponseWrapper<List<UserPermissionEntity>>

    /**
     * 修改用户信息接口
     */
    @POST("/user/updateUserInfo")
    suspend fun updateUserInfo(@Body body: RequestBody): ResponseWrapper<Any>

    @Multipart
    @POST("/common/upload")
    suspend fun updateLoadFile(@Part file: MultipartBody.Part): ResponseWrapper<String>
}