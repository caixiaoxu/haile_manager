package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/16 15:37
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HaixinRechargeEntity(
    val account: String,
    val amount: Double,
    val createTime: String,
    val id: Int,
    val orderNo: String,
    val tokenCoinAmount: Int
) {

    fun timeStr(): String = DateTimeUtils.formatDateTimeForStr(createTime, "MM/dd HH:mm:ss")

    fun amountStr(): String = StringUtils.formatNumberStr(amount)
}

data class HaixinRechargeListEntity(
    val month: Date?,
    val haiXinTotalEntity: HaiXinTotalEntity?,
    val haixinRechargeEntity: HaixinRechargeEntity?
)