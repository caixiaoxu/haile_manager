package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.RechargeType
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
data class RechargeDetailEntity(
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
) : IIncomeDetailEntity {

    override fun mainRes(): Int = RechargeType.getMainRes(type, subType)

    override fun getTotalStr(): String = (if (100 == type) amount else -amount).toString()

    override fun getTag(): String = typeDesc

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
            "$principalAmount"
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.reward_starfish),
            "$presentAmount"
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.time_of_payment),
            businessTime
        ),
    )

    override fun canGoOrderDetail(): Boolean = false

}