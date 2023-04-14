package com.shangyun.haile_manager_android.utils

import android.content.Intent
import com.lsy.framelib.data.constants.Constants
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.ui.activity.login.LoginActivity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/12 16:09
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ActivityManagerUtils {

    /**
     * 清空登录信息，跳转到登录界面
     */
    fun clearLoginInfoGoLogin(){
        // 清空登录信息
        SPRepository.cleaLoginUserInfo()
        // 跳转登录界面
        Constants.APP_CONTEXT.startActivity(
            Intent(
                Constants.APP_CONTEXT,
                LoginActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}