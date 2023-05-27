package com.shangyun.haile_manager_android.data.arguments

import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.rule.ICalendarEntity
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import java.util.Calendar
import java.util.Date

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/27 11:37
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CalendarEntity(
    override val dayNum: String,
    override val type: Int
) : ICalendarEntity {
    var curDate: Date? = null
        get() = Date()
    override val value: String
        get() = ""
    override val color: IntArray
        get() = intArrayOf(
            ContextCompat.getColor(
                Constants.APP_CONTEXT,
                R.color.color_gray
            ),
            ContextCompat.getColor(
                Constants.APP_CONTEXT,
                R.color.colorPrimary
            ),
            ContextCompat.getColor(
                Constants.APP_CONTEXT,
                R.color.color_green
            ),
        )
}