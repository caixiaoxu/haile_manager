package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.extend.toRemove0Str
import com.yunshang.haile_manager_android.data.rule.IIncomeDetailEntity
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/30 11:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class RechargeOrderDetailEntity(
    val account: String,
    val amount: String,
    val orderNo: String,
    val payTime: String,
    val presentAmount: Double,
    val principalAmount: Double,
    val rechargeType: Int
) : IIncomeDetailEntity {

    override fun mainRes(): Int = R.mipmap.icon_haixin_recharge_list_main

    override fun getTotalStr(): String = amount

    override fun getTag(): String = "充值订单"

    override fun getInfoList(): ArrayList<IncomeDetailInfo> = arrayListOf(
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.order_no),
            orderNo,
            true
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.user_account_no),
            account
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.reach_starfish),
            "$principalAmount".toRemove0Str()
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.reward_starfish),
            "$presentAmount".toRemove0Str()
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.time_of_payment),
            payTime
        ),
    )

    override fun canGoOrderDetail(): Boolean = false

}