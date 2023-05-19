package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.arguments.DiscountsParam
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.utils.DateTimeUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/19 13:54
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DiscountsDetailEntity(
    val bizType: Int,
    val bizTypeName: String,
    val buildTime: String,
    val categoryList: List<DiscountsDeviceTypeEntity>,
    val createTime: String,
    val createUserId: Int,
    val createUserName: String,
    val discount: Double,
    val discountEnd: String,
    val discountStart: String,
    val discountVO: String,
    val id: Int,
    val operatorId: Int,
    val realName: String,
    val shop: List<SearchSelectParam>,
    val status: Int,
    val timeSpan: String,
    val weekDayList: List<Int>,
    val weekDayMode: Int
) {
    val shopStr: String
        get() = shop.joinToString("，") { it.name }

    val hasBizType: Boolean
        get() = !bizTypeName.isNullOrEmpty()

    val categoryStr: String
        get() = if (categoryList.isNullOrEmpty()) "" else categoryList.joinToString("，") { it.name }

    val hasCategory: Boolean
        get() = !categoryList.isNullOrEmpty()

    val startDateStr: String
        get() = DateTimeUtils.formatDateTimeForStr(discountStart, "yyyy/MM/dd")

    val hasStartDate: Boolean
        get() = !discountStart.isNullOrEmpty()

    val endDateStr: String
        get() = DateTimeUtils.formatDateTimeForStr(discountEnd, "yyyy/MM/dd")
    val hasEndDate: Boolean
        get() = !discountEnd.isNullOrEmpty()
    val weekDayModeStr: String
        get() = DiscountsParam.activeModel.find { it.id == weekDayMode }?.name ?: ""
    val hasWeekDayMode: Boolean
        get() = weekDayModeStr.isNotEmpty()
    val weekDayStr: String
        get() = weekDayList.joinToString(" ") { id ->
            DiscountsParam.activeDay.find { it.id == id }?.name ?: ""
        }
    val hasWeekDay: Boolean
        get() = weekDayStr.isNotEmpty()
    val hasTimeSpan: Boolean
        get() = !timeSpan.isNullOrEmpty()
    val hasDiscount: Boolean
        get() = !discountVO.isNullOrEmpty()
}