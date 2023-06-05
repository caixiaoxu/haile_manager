package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/30 14:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class BalanceEntity(
    val balance: String,
    val balanceType: Int,
    val businessType: String,
    val capital: String,
    val cashOutTime: String,
    val category: String,
    val id: Int,
    val settlementTime: String,
    val title: String
) {
    fun isPlusOrMinus(): Boolean = 1 == balanceType

    fun timeStr(): String = DateTimeUtils.formatDateTimeForStr(cashOutTime, "MM/dd HH:mm:ss")

    fun amountStr(): String = StringUtils.formatNumberStrOfStr(capital) ?: ""
}

data class BalanceListEntity(val month: Date?, val balanceEntity: BalanceEntity?)