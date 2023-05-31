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
    override fun isPlusOrMinus(): Boolean = 1 == balanceType

    override fun getTotalStr(): String = StringUtils.formatNumberStrOfStr(capital) ?: ""

    override fun getTag(): String = title

    override fun getInfoList(): ArrayList<IncomeDetailInfo> = ArrayList<IncomeDetailInfo>().apply {
        addItemInfo(businessType, R.string.business_name, this)
        addItemInfo(categoryName, R.string.business_type, this)
        addItemInfo(shopName, R.string.shop_manager, this)
        addItemInfo(settlementTime, R.string.time, this)
        if (revenueNo.isNotEmpty() && revenueTime.isNotEmpty()) {
            addItemInfo(revenueNo, R.string.sub_account_order_no, this, true)
            addItemInfo(revenueTime, R.string.sub_account_time, this, true)
        } else if (cashOutNo.isNotEmpty()) {
            addItemInfo(cashOutNo, R.string.cash_out_no, this, true)
            addItemInfo(cashOutPrice, R.string.cash_out_amount, this)
            addItemInfo(serviceCharge, R.string.service_charge, this)
            addItemInfo(cashOutTime, R.string.cash_out_time, this)
            addItemInfo(bank, R.string.cash_out_bank, this)
        } else {
            addItemInfo(orderNo, R.string.order_no, this, true)
        }
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
