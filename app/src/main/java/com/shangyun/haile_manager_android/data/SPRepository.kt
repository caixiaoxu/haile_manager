package com.shangyun.haile_manager_android.data

import android.text.TextUtils
import com.lsy.framelib.utils.SPUtils

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
    fun isLogin(): Boolean = !TextUtils.isEmpty(loginToken)

    /**
     * 获取登录Token
     */
    var loginToken: String
        get() = sp.getString(SP_LOGIN_TOKEN) ?: ""
        set(value) = sp.put(SP_LOGIN_TOKEN, value)

    /**
     * 获取用户Token
     */
    var userToken: String
        get() = sp.getString(SP_USER_TOKEN) ?: ""
        set(value) = sp.put(SP_USER_TOKEN, value)
}