package com.lsy.framelib.utils

import android.content.Context
import android.widget.Toast
import com.lsy.framelib.data.constants.Constants

/**
 * Title : Toast工具类
 * Author: Lsy
 * Date: 2023/4/3 18:22
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object SToast {

    /**
     * 显示Toast
     * @param context 上下文(不传就使用ApplicationContext)
     * @param msg 内容
     * @param duration 时长（可选）
     */
    fun showToast(
        context: Context? = Constants.APP_CONTEXT,
        msg: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(context, msg, duration).show()
    }
    /**
     * 显示Toast
     * @param context 上下文(不传就使用ApplicationContext)
     * @param msgResId 内容resId
     * @param duration 时长（可选）
     */
    fun showToast(
        context: Context? = Constants.APP_CONTEXT,
        msgResId: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(context, msgResId, duration).show()
    }
}