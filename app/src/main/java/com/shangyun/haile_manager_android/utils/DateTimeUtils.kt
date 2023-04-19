package com.shangyun.haile_manager_android.utils

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

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
     * 获取小时列表
     */
    fun getHourSection(): List<String> =
        ArrayList<String>().apply {
            for (i in 0 .. 23) {
                add("${i}时")
            }
        }

    /**
     * 获取分钟列表
     */
    fun getMinuteSection(): List<String> =
        ArrayList<String>().apply {
            for (i in 0 .. 59) {
                add("${i}分")
            }
        }

    /**
     * 获取秒列表
     */
    fun getSecondSection(): List<String> =
        ArrayList<String>().apply {
            for (i in 0 .. 59) {
                add("${i}分")
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

    /**
     * 是否两个时间是否为同一天
     *
     * @param date1,date2 两个对比时间
     */
    private fun isSameDate(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        val isSameYear = cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
        val isSameMonth = (isSameYear
                && cal1[Calendar.MONTH] == cal2[Calendar.MONTH])
        return (isSameMonth
                && cal1[Calendar.DAY_OF_MONTH] == cal2[Calendar.DAY_OF_MONTH])
    }

    /**
     * 天数增加
     *
     * @param date 时间
     * @param day  增加天数 可为负整数、正整数,例如-1 相当于减少一天,1 相当于增加一天
     */
    fun addDay(date: Date, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.DATE] = calendar[Calendar.DATE] + day
        return calendar.time
    }

    /**
     * 获取友好时间
     *
     * @param date 时间
     * @param isAf 是否显示将来时间
     */
    fun getFriendlyTime(date: Date?, isAf: Boolean): String {
        if (date == null) {
            return ""
        }
        val ct = ((System.currentTimeMillis() - date.time) / 1000).toInt()
        if (isAf) {
            if (ct < 0) {
                if (ct < -86400) { //86400 * 30
                    val day = ct / -86400
                    if (day == 1) {
                        return "后天 " + formatDateTime("HH:mm", date)
                    }
                    return if (date.year == Date().year) {
                        formatDateTime("MM-dd HH:mm", date)
                    } else {
                        formatDateTime("yyyy-MM-dd HH:mm", date)
                    }
                }
                if (!isSameDate(Date(), date)
                ) { //判断跨天
                    return "明天 " + formatDateTime("HH:mm", date)
                }
                if (-86400 < ct && ct < -3600) {
                    return String.format(
                        "%d小时后 %s",
                        ct / -3600,
                        formatDateTime("HH:mm", date)
                    )
                }
                if (-3600 < ct && ct < -61) {
                    return String.format(
                        "%d分钟后 %s",
                        max(ct / -60, 3),
                        formatDateTime("HH:mm", date)
                    )
                }
                if (-61 < ct && ct < 0) {
                    return String.format("即将到点 %s", formatDateTime("HH:mm", date))
                }
            }
        }
        if (ct < 61) { //1分钟内
            return "刚刚"
        }
        if (isSameDate(Date(), date)) { //
            if (ct < 3600) { //1小时内
                return String.format("%d分钟前", max(ct / 60, 3))
            }
            if (ct in 3600..86399) { //当天超过一小时显示
                return formatDateTime("HH:mm", date)
            }
        } else {
            //不是当天时进入
            if (ct < 86400) {
                return "昨天 " + formatDateTime("HH:mm", date)
            }
            if (ct in 86400..259199) { //86400 * 3 (三天)
                var day = ct / 86400
                if (!isSameDate(addDay(Date(), -day), date)
                ) { //判断时间匹配日期
                    day += 1
                }
                return if (day == 1) {
                    "昨天 " + formatDateTime("HH:mm", date)
                } else {
                    formatDateTime("MM-dd HH:mm", date)
                }
            }
        }
        return if (date.year == Date().year) {
            formatDateTime("MM-dd HH:mm", date)
        } else {
            formatDateTime("yyyy-MM-dd HH:mm", date)
        }
    }
}