package com.shangyun.haile_manager_android.utils

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Title : 日期时间工具类
 * Author: Lsy
 * Date: 2023/3/21 19:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
object DateTimeUtils {
    /**
     * 当月第一天
     */
    @JvmStatic
    val curMonthFirst: Date
        get() = getMonthFirst(Date())

    /**
     * 指定日期当月的第一天
     */
    @JvmStatic
    fun getMonthFirst(date: Date?): Date {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        cal[Calendar.DAY_OF_MONTH] = 1
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        return cal.time
    }

    /**
     * 当月的最后一天
     */
    @JvmStatic
    val curMonthLast: Date
        get() = getMonthLast(Date())

    /**
     * 指定日期当月的最后一天
     */
    @JvmStatic
    fun getMonthLast(date: Date?): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.DAY_OF_MONTH] = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal[Calendar.HOUR_OF_DAY] = 23
        cal[Calendar.MINUTE] = 59
        cal[Calendar.SECOND] = 59
        return cal.time
    }

    /**
     * 前一月
     */
    @JvmStatic
    fun beforeMonth(date: Date?): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.MONTH, -1)
        return cal.time
    }

    /**
     * 格式化
     */
    @JvmStatic
    fun formatDateTime(date: Date?): String {
        return formatDateTime("yyyy-MM-dd HH:mm:ss", date)
    }

    /**
     * 格式化
     */
    @JvmStatic
    fun formatDateTime(format: String?, date: Date?): String {
        return if (null == date) "" else DateFormat.format(format, date).toString()
    }

    /**
     * 反格式化
     */
    @JvmStatic
    fun formatDateFromString(dateStr: String?): Date? {
        return if (null == dateStr) null else formatDateFromString(
            "yyyy-MM-dd HH:mm:ss",
            dateStr
        )
    }

    /**
     * 反格式化
     */
    @JvmStatic
    fun formatDateFromString(format: String?, dateStr: String?): Date? {
        val sdf = SimpleDateFormat(format, Locale.CHINA)
        try {
            return sdf.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取年区间列表
     */
    fun getYearSection(minYear: Int, maxYear: Int): List<String> =
        ArrayList<String>().apply {
            for (i in 0 until (maxYear - minYear + 1)) {
                add("${minYear + i}年")
            }
        }

    /**
     * 获取月份列表
     */
    fun getMonthSection(): List<String> =
        ArrayList<String>().apply {
            for (i in 1 until 13) {
                add("${i}月")
            }
        }

    /**
     * 获取天份列表
     */
    fun getDaySection(maxDay: Int): List<String> =
        ArrayList<String>().apply {
            for (i in 1 until (maxDay + 1)) {
                add("${i}日")
            }
        }
}