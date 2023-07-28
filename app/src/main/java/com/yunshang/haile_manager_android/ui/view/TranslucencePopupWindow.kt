package com.yunshang.haile_manager_android.ui.view

import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow


/**
 * Title : 半透明背景popupwindow
 * Author: Lsy
 * Date: 2023/7/24 18:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class TranslucencePopupWindow(
    contentView: View?,
    private val window: Window,
    width: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    alpha: Float = 0.4f
) :
    PopupWindow(contentView, width, height) {

    init {
        isTouchable = true
        isFocusable = true
        isOutsideTouchable = true
        backgroundAlpha(alpha)

        setOnDismissListener {
            backgroundAlpha(1f)
        }
    }

    /**
     * 设置背景透明
     */
    private fun backgroundAlpha(alpha: Float) {
        window.attributes.let { windowAttr ->
            windowAttr.alpha = alpha
            if (alpha == 1f) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //此行代码主要是解决在华为手机上半透明效果无效的bug
            }
            window.attributes = windowAttr
        }
    }
}