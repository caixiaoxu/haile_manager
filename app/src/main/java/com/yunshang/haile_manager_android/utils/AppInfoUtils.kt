package com.yunshang.haile_manager_android.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager


/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/13 20:50
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object AppInfoUtils {

    /**
     *  获取当前程序的版本号
     */
    fun getVersionName(context: Context): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}