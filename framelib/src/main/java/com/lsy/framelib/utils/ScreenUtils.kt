package com.lsy.framelib.utils

import android.content.res.Resources

/**
 * Title : 屏幕信息工具类
 * Author: Lsy
 * Date: 2023/4/10 11:45
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ScreenUtils {

    /**
     * 获取屏幕的宽
     */
    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    /**
     * 获取屏幕的高
     */
    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels
}