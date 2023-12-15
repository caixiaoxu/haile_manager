package com.yunshang.haile_manager_android.data.arguments

import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.entities.IncomeCalendarEntity
import com.yunshang.haile_manager_android.data.extend.formatMoney
import com.yunshang.haile_manager_android.data.rule.ICalendarEntity
import com.yunshang.haile_manager_android.utils.DateTimeUtils
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
data class CalendarEntity(override var type: Int, val day: String? = null) : ICalendarEntity {
    override var value: String = if (-1 == type) "" else "--"

    fun initIncome(incomeEntity: IncomeCalendarEntity?) {
        incomeEntity?.let {
            type = if (incomeEntity.amount >= 0) 1 else 2
            value = "${incomeEntity.amount.formatMoney()}"
        }
    }

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

    override fun getDate(): Date? = DateTimeUtils.formatDateFromString(day)
    override fun isSelect(date: Date): Boolean = day == DateTimeUtils.formatDateTimeStartParam(date)
    override fun afterToday(): Boolean =
        DateTimeUtils.formatDateFromString(day)?.time?.let { it > System.currentTimeMillis() }
            ?: false

    override fun getDayNum(): String? =
        day?.let {
            "${
                Calendar.getInstance().apply {
                    DateTimeUtils.formatDateFromString(day)?.let {
                        time = it
                    }
                }.get(Calendar.DAY_OF_MONTH)
            }"
        }
}