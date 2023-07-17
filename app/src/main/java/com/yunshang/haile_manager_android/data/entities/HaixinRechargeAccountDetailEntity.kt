package com.yunshang.haile_manager_android.data.entities

import android.text.TextUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.RechargeType
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/16 17:33
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HaixinRechargeAccountDetailEntity(
    val account: String,
    val afterAmount: Int,
    val amount: Int,
    val businessTime: String,
    val createTime: String,
    val id: Int,
    val orderNo: String,
    val presentAmount: Int,
    val principalAmount: Int,
    val remark: String,
    val shopId: Int,
    val subType: Int,
    val type: Int,
    val typeDesc: String,
    val userId: Int
) {

    fun getMainRes(): Int = RechargeType.getMainRes(type, subType)

    fun timeStr(): String = DateTimeUtils.formatDateTimeForStr(createTime, "MM/dd HH:mm:ss")

    fun getAmountVal(): String = if (100 == type) "$amount" else "-$amount"

    fun getDesc(): String {

        var desc = ""
        if (principalAmount > 0) {
            desc += "${StringUtils.getString(R.string.reach_starfish)} $principalAmount"
        }
        if (presentAmount > 0) {
            desc += (if (TextUtils.isEmpty(desc)) "" else " ") + "${StringUtils.getString(R.string.reward_starfish)} $presentAmount"
        }
        return desc
    }

}

data class HaixinRechargeAccountDetailsEntity(
    val month: Date?,
    val haiXinRechargeAccountDetailTotal: HaixinRechargeAccountEntity?,
    val haixinRechargeAccountDetailEntity: HaixinRechargeAccountDetailEntity?
)