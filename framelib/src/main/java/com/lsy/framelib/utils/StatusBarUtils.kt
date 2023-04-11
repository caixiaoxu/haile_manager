package com.lsy.framelib.utils

import android.content.res.Resources
import android.view.View
import androidx.core.view.ViewCompat

/**
 * Title : 状态栏工具类
 * Author: Lsy
 * Date: 2023/4/3 10:41
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object StatusBarUtils {

    /**
     * Return the status bar's height.
     * 返回状态栏的高度
     *
     * @return the status bar's height
     */
    fun getStatusBarHeight(): Int {
        val resources: Resources = Resources.getSystem()
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }


    /**
     * 状态状态栏文字颜色
     */
    fun setStatusBarDarkTheme(view: View, dark: Boolean) {
        val controller = ViewCompat.getWindowInsetsController(view)
        controller?.isAppearanceLightStatusBars = dark
    }
}