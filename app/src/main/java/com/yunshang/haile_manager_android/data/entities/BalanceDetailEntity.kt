package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IIncomeDetailEntity
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo
import com.yunshang.haile_manager_android.utils.StringUtils

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
    val balance: String,
    val balanceType: Int,
    val bank: String,
    val businessType: String,
    val capital: String,
    val cashOutNo: String,
    val cashOutPrice: String,
    val cashOutTime: String,
    val category: String,
    val categoryName: String,
    val id: Int,
    val orderNo: String,
    val payTime: String,
    val remark: String,
    val revenueNo: String = "2342342342342342",
    val revenueTime: String = "时间",
    val serviceCharge: String,
    val settlementTime: String,
    val shopName: String,
    val title: String,
    val transactionSubType: Int
) : IIncomeDetailEntity {

    override fun mainRes(): Int =
        if (1 == balanceType) R.mipmap.icon_income_main else R.mipmap.icon_expend_main

    override fun getTotalStr(): String = StringUtils.formatNumberStrOfStr(capital) ?: ""

    override fun getTag(): String = title

    override fun getInfoList(): ArrayList<IncomeDetailInfo> = ArrayList<IncomeDetailInfo>().apply {
        addItemInfo(businessType, R.string.business_name, this)
        addItemInfo(
            if (1 == balanceType) com.lsy.framelib.utils.StringUtils.getString(R.string.earning)
            else com.lsy.framelib.utils.StringUtils.getString(R.string.expend),
            R.string.income_type,
            this
        )
        addItemInfo(categoryName, R.string.business_type, this)
        addItemInfo(shopName, R.string.shop, this)
        addItemInfo(settlementTime, R.string.settlement_time, this)
        if (revenueNo.isNotEmpty() && revenueTime.isNotEmpty()) {
            addItemInfo(revenueNo, R.string.sub_account_order_no, this, true)
            addItemInfo(revenueTime, R.string.sub_account_time, this)
        } else if (cashOutNo.isNotEmpty()) {
            addItemInfo(cashOutNo, R.string.cash_out_no, this, true)
            addItemInfo(cashOutPrice, R.string.cash_out_amount, this)
            addItemInfo(serviceCharge, R.string.service_charge, this)
            addItemInfo(cashOutTime, R.string.cash_out_time, this)
            addItemInfo(bank, R.string.cash_out_way, this)
        } else {
            addItemInfo(account, R.string.user_account_no, this, false)
            addItemInfo(payTime, R.string.time_of_payment, this, false)
            addItemInfo(orderNo, R.string.order_no, this, true)
        }
        addItemInfo(
            balance,
            R.string.account_balance,
            this
        )
        addItemInfo(remark, R.string.remark, this)
    }

    private fun addItemInfo(
        content: String,
        titleRes: Int,
        infos: ArrayList<IncomeDetailInfo>,
        canCopy: Boolean = false
    ) {
        if (content.isNotEmpty()) {
            infos.add(
                IncomeDetailInfo(
                    com.lsy.framelib.utils.StringUtils.getString(titleRes),
                    content, canCopy
                )
            )
        }
    }

    override fun canGoOrderDetail(): Boolean = false
}
