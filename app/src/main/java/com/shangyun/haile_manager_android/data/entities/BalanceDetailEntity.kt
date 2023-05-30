package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.rule.IIncomeDetailEntity
import com.shangyun.haile_manager_android.data.rule.IncomeDetailInfo
import com.shangyun.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/30 17:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class BalanceDetailEntity(
    val account: String,
    val balance: Double,
    val balanceType: Int,
    val bank: String,
    val businessType: String,
    val capital: Double,
    val cashOutNo: String,
    val cashOutPrice: Double,
    val cashOutTime: String,
    val category: String,
    val categoryName: String,
    val id: Int,
    val orderNo: String,
    val payTime: String,
    val remark: String,
    val revenueNo: String,
    val revenueTime: String,
    val serviceCharge: Double,
    val settlementTime: String,
    val shopName: String,
    val title: String,
    val transactionSubType: Int
) : IIncomeDetailEntity {
    override fun isPlusOrMinus(): Boolean = 1 == balanceType

    override fun getTotalStr(): String = StringUtils.formatNumberStr(capital)

    override fun getTag(): String = title

    override fun getInfoList(): ArrayList<IncomeDetailInfo> = ArrayList<IncomeDetailInfo>().apply {
        val isCashOut = transactionSubType in 20010..20020
        if (!isCashOut) {
            add(
                IncomeDetailInfo(
                    com.lsy.framelib.utils.StringUtils.getString(R.string.order_no),
                    orderNo
                )
            )
            if (account.isNotEmpty()){
                add(
                    IncomeDetailInfo(
                        com.lsy.framelib.utils.StringUtils.getString(R.string.user_account),
                        account
                    )
                )
            }
            if (payTime.isNotEmpty()){
                add(
                    IncomeDetailInfo(
                        com.lsy.framelib.utils.StringUtils.getString(R.string.pay_time),
                        payTime
                    )
                )
            }
            if (settlementTime.isNotEmpty()){
                add(
                    IncomeDetailInfo(
                        com.lsy.framelib.utils.StringUtils.getString(R.string.settlement_time),
                        settlementTime
                    )
                )
            }
        } else {
            add(
                IncomeDetailInfo(
                    com.lsy.framelib.utils.StringUtils.getString(R.string.cash_out_no),
                    cashOutNo
                )
            )
            add(
                IncomeDetailInfo(
                    com.lsy.framelib.utils.StringUtils.getString(R.string.cash_out_amount),
                    "$cashOutPrice"
                )
            )
            add(
                IncomeDetailInfo(
                    com.lsy.framelib.utils.StringUtils.getString(R.string.service_charge),
                    "$serviceCharge"
                )
            )
            add(
                IncomeDetailInfo(
                    com.lsy.framelib.utils.StringUtils.getString(R.string.cash_out_time),
                    cashOutTime
                )
            )
            //提现银行
            if (bank.isNotEmpty()) {
                add(
                    IncomeDetailInfo(
                        com.lsy.framelib.utils.StringUtils.getString(R.string.cash_out_bank),
                        bank
                    )
                )
            }
        }
        add(
            IncomeDetailInfo(
                com.lsy.framelib.utils.StringUtils.getString(R.string.user_balance),
                "$balance"
            )
        )
    }

    override fun canGoOrderDetail(): Boolean = false
}
