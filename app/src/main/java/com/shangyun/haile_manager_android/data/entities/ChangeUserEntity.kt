package com.shangyun.haile_manager_android.data.entities

import androidx.annotation.IntDef
import com.shangyun.haile_manager_android.data.model.SPRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/12 10:54
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ChangeUserEntity(
    @LoginType val loginType: Int,// 登录类型 0账号密码 1手机号验证码 2一键登录
    val password: String?,//登录密码
    val loginInfo: LoginEntity,//登录信息
    val userInfo: UserInfoEntity,//用户信息
){
    /**
     * 是否是当前账号
     */
    fun isCurUser():Boolean = loginInfo.userId == SPRepository.loginInfo?.userId
}

@IntDef(PASSWORD, CODE, ONE)
@Retention(AnnotationRetention.SOURCE)
annotation class LoginType

const val PASSWORD: Int = 0
const val CODE: Int = 1
const val ONE: Int = 2



