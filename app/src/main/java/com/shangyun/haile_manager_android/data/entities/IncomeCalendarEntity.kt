package com.shangyun.haile_manager_android.data.entities

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
data class IncomeCalendarEntity(
    val amount: Double,
    val date: String,
    val dateWeek: String
)