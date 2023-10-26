package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
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
     * 用户授权权限接口
     */
    @POST("/permission/authorizationMenu")
    suspend fun permissionMenu(): ResponseWrapper<List<UserPermissionEntity>>

    /**
     * 修改用户信息接口
     */
    @POST("/user/updateUserInfo")
    suspend fun updateUserInfo(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 获取用户角色接口
     */
    @POST("/login/getLoginUserList")
    suspend fun requestRoleInfo(@Body body: RequestBody): ResponseWrapper<List<RoleEntity>>

    /**
     * 切换用户角色接口
     */
    @POST("/login/swapUserLogin")
    suspend fun swapUserLogin(@Body body: RequestBody): ResponseWrapper<LoginEntity>

    /**
     * 查询实名认证接口
     */
    @GET("/user/verifyDetail")
    suspend fun requestRealNameAuthDetail(): ResponseWrapper<RealNameAuthDetailEntity>

    /**
     * 实名认证接口
     */
    @POST("/user/verify")
    suspend fun verifyRealNameAuth(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 用户注销接口
     */
    @POST("/user/logout")
    suspend fun cancelAccount(): ResponseWrapper<Any>

    /**
     * 注册接口
     */
    @POST("/merchant/reg")
    suspend fun register(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 注册验证码接口
     */
    @POST("/merchant/sendSmsCode")
    suspend fun sendRegisterCode(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 修改登录手机号验证码接口
     */
    @POST("/user/phone/sendCode")
    suspend fun sendChangeLoginPhoneCode(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 校验登录手机号验证码接口
     */
    @POST("/user/phone/checkCode")
    suspend fun checkChangeLoginPhoneCode(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 修改登录手机号接口
     */
    @POST("/user/updatePhone")
    suspend fun changeLoginPhone(@Body body: RequestBody): ResponseWrapper<Any>
}