package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/12 16:38
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class WithdrawAccountEntity(
    val cashOutAccount: String,
    val cashOutRate: Int,
    val cashOutType: Int,
    val exist: Boolean,
    val id: Int,
    val maxWithdrawAmount: String,
    val realName: String,
    val state: Int
) {

    val cashOutAccountVal: String
        get() = if (null != cashOutAccount && cashOutAccount.length > 7)
            com.yunshang.haile_manager_android.utils.StringUtils.formatPhone(cashOutAccount)
        else cashOutAccount

    fun cashOutLimit(): String = try {
        val max = maxWithdrawAmount.toDouble()
        val maxVal = (if (max > 10000) max / 10000 else max).let {
            if ((it * 100).toInt() % 100 == 0) it.toInt() else it
        }
        StringUtils.getString(
            R.string.alipay_withdraw_hint,
            if (max > 10000) "${maxVal}万" else "$maxVal"
        )
    } catch (e: Exception) {
        ""
    }
}