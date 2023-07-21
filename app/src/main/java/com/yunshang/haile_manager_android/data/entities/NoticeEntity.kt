package com.yunshang.haile_manager_android.data.entities

import android.graphics.Color
import com.yunshang.haile_manager_android.utils.DateTimeUtils

/**
 * Title :
 * Author: gdz
 * Date: 2023/5/16 14:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class NoticeEntity(
    val id: Int,
    val templateId: Int,
    val startTime: String,
    val endTime: String,
    val state: Int,
    val userId: Int,
    val version: String,
    val deleteFlag: Int,
    val createTime: String,
    val account: String,
    val templateName: String,
    val templateStartTime: String,
    val templateEndTime: String,
) {
    val time: String
        get() = DateTimeUtils.formatDateTimeForStr(
            startTime,
            "yyyy/MM/dd HH:mm"
        ) + " - " + DateTimeUtils.formatDateTimeForStr(endTime, "yyyy/MM/dd HH:mm")


    fun statename(): String {
        var statename: String
        when (state) {
            0 -> {
                statename = "未开始"
            }

            1 -> {
                statename = "进行中"
            }

            2 -> {
                statename = "已结束"
            }

            else -> {
                statename = ""
            }
        }
        return statename
    }

    fun statecolor(): Int {
        var statecolor: String
        when (state) {
            0 -> {
                statecolor = "#333333"
            }

            1 -> {
                statecolor = "#F0A258"
            }

            2 -> {
                statecolor = "#999999"
            }

            else -> {
                statecolor = "#999999"
            }
        }
        return Color.parseColor(statecolor)
    }
}