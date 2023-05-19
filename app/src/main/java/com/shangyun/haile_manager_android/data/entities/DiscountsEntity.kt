package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.arguments.ActiveDayParam
import com.shangyun.haile_manager_android.data.arguments.DiscountsParam
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.utils.DateTimeUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/18 10:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DiscountsEntity(
    val bizType: Int,
    val bizTypeName: String,
    val categoryList: List<SearchSelectParam>,
    val discount: Double,
    val discountEnd: String,
    val discountStart: String,
    val discountVO: String,
    val expired: Int,
    val id: Int,
    val shop: List<SearchSelectParam>,
    val status: Int,
    val timeSpan: String,
    val timeStatus: Int,
    val weekDayList: List<Int>,
    val weekDayMode: Int
) {
    fun getDiscountTime(): String = "" +
            "${
                DateTimeUtils.formatDateTimeForStr(
                    discountStart,
                    "yyyy.MM.dd"
                )
            }-${DateTimeUtils.formatDateTimeForStr(discountEnd, "yyyy.MM.dd")}"

    fun getDiscountStatusVal(): String = DiscountsParam.getStatusVal(status, expired)

    fun getShopStr(): String = shop.joinToString("，") { it.name }
}