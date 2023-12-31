package com.yunshang.haile_manager_android.data.rule

import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.R
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/27 11:18
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface ICalendarEntity {
    var type: Int // 默认-1:不显示，0:无值，1:正值，2:负值，3:0值
    val color: IntArray

    fun getDate(): Date?

    fun isSelect(date: Date): Boolean

    fun afterToday(): Boolean

    fun getDayNum(): String?

    var value: String

    /**
     * 获取当前的类型颜色
     */
    val curTypeBGColor: Int
        get() = color[if (0 == type || 3 == type) 1 else type]

    /**
     * 获取当前的类型颜色
     */
    val curTypeColor: Int
        get() = if (-1 != type && color.size > type) color[type] else ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.common_sub_txt_color
        )
}
