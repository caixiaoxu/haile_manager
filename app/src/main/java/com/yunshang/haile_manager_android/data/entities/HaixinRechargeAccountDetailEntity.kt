package com.yunshang.haile_manager_android.data.entities

import android.text.TextUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
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
    val afterAmount: Double,
    val amount: Double,
    val businessTime: String,
    val createTime: String,
    val id: Int,
    val orderNo: String,
    val presentAmount: Double,
    val principalAmount: Double,
    val remark: String,
    val shopId: Int,
    val subType: Int,
    val type: Int,
    val typeDesc: String,
    val userId: Int
) {

    fun getMainRes():Int {
        var mainRes:Int
        if (100 == type){
            mainRes = R.mipmap.icon_haixin_recharge_list_main
        } else {
            mainRes = R.mipmap.icon_haixin_expense
        }

        if (104 == subType){
            mainRes = R.mipmap.icon_haixin_refund
        } else if (202 == subType){
            mainRes = R.mipmap.icon_haixin_recycle
        }
        return mainRes
    }

    fun timeStr(): String = DateTimeUtils.formatDateTimeForStr(createTime, "MM/dd HH:mm:ss")

    fun getAmountVal(): String =
        com.yunshang.haile_manager_android.utils.StringUtils.formatNumberStr(if (100 == type) amount else -amount)

    fun getDesc(): String {

        var desc = ""
        if (principalAmount > 0) {
            desc += "${StringUtils.getString(R.string.reach_starfish)} $principalAmount"
        }
        if (presentAmount > 0) {
            desc += if (TextUtils.isEmpty(desc)) "" else " ${StringUtils.getString(R.string.reward_starfish)} $presentAmount"
        }
        return desc;
    }

}

data class HaixinRechargeAccountDetailsEntity(
    val month: Date?,
    val haiXinRechargeAccountDetailTotal: HaixinRechargeAccountEntity?,
    val haixinRechargeAccountDetailEntity: HaixinRechargeAccountDetailEntity?
)