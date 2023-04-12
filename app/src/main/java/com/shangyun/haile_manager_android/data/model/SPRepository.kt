package com.shangyun.haile_manager_android.data.model

import com.lsy.framelib.utils.SPUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.data.entities.ChangeUserEntity
import com.shangyun.haile_manager_android.data.entities.LoginEntity
import com.shangyun.haile_manager_android.data.entities.UserInfoEntity
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/31 16:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object SPRepository {
    private const val SP_NAME: String = "default_name"

    private const val SP_AGREE_AGREEMENT = "sp_agree_agreement"

    private const val SP_LOGIN_TOKEN = "sp_login_token"

    private const val SP_USER_TOKEN = "sp_user_token"

    private const val SP_USER_PERMISSION = "sp_user_permission"

    private const val SP_CHANGE_USER = "sp_change_user"

    private val sp: SPUtils by lazy { SPUtils.getInstance(SP_NAME) }

    /**
     * 判断是否同意了隐私协议
     */
    var isAgreeAgreement: Boolean
        get() = sp.getBoolean(SP_AGREE_AGREEMENT, false)
        set(value) = sp.put(SP_AGREE_AGREEMENT, value)

    /**
     * 判断是否已登录
     */
    fun isLogin(): Boolean = null != loginInfo && null != userInfo

    /**
     * 获取登录Token
     */
    var loginInfo: LoginEntity? = null
        get() {
            return field ?: GsonUtils.json2Class(
                sp.getString(SP_LOGIN_TOKEN),
                LoginEntity::class.java
            )
        }
        set(value) {
            if (null == value) {//清空缓存
                sp.put(SP_LOGIN_TOKEN, "")
            } else {//重新设置缓存
                sp.put(SP_LOGIN_TOKEN, GsonUtils.any2Json(value))
            }
            //清空用户信息
            userInfo = null
            //清空用户权限
            userPermissions = null
            field = value
        }

    /**
     * 获取用户Token
     */
    var userInfo: UserInfoEntity? = null
        get() {
            return field ?: GsonUtils.json2Class(
                sp.getString(SP_USER_TOKEN),
                UserInfoEntity::class.java
            )
        }
        set(value) {
            if (null == value) {//清空缓存
                sp.put(SP_USER_TOKEN, "")
            } else {//重新设置缓存
                sp.put(SP_USER_TOKEN, GsonUtils.any2Json(value))
            }
            field = value
        }

    /**
     * 获取用户权限
     */
    var userPermissions: List<UserPermissionEntity>? = null
        get() {
            return field ?: GsonUtils.json2List(
                sp.getString(
                    SP_USER_PERMISSION
                ), UserPermissionEntity::class.java
            )
        }
        set(value) {
            if (null == value) {//清空缓存
                sp.put(SP_USER_PERMISSION, "")
            } else {//重新设置缓存
                sp.put(SP_USER_PERMISSION, GsonUtils.any2Json(value))
            }
            field = value
        }

    var changeUser:MutableList<ChangeUserEntity>? = null
        get() {
            return field ?: GsonUtils.json2List(
                sp.getString(
                    SP_CHANGE_USER
                ), ChangeUserEntity::class.java
            ) ?: arrayListOf()
        }
        set(value) {
            if (null == value) {//清空缓存
                sp.put(SP_CHANGE_USER, "")
            } else {//重新设置缓存
                sp.put(SP_CHANGE_USER, GsonUtils.any2Json(value))
            }
            field = value
        }

    /**
     * 清空登录和用户信息
     */
    fun cleaLoginUserInfo() {
        loginInfo = null
    }
}