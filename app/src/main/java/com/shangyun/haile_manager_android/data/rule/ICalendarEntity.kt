package com.shangyun.haile_manager_android.data.rule

import android.graphics.Color

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
    val dayNum: String
    val value: String
    val type: Int // 默认-1不显示
    val color: IntArray

    /**
     * 获取当前的类型颜色
     */
    val curTypeColor: Int
        get() = if (color.size < type) color[type] else Color.parseColor("#333333")
}
