package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IIncomeDetailEntity
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/13 16:18
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class WithdrawDetailEntity(
    val cashOutPrice: Double,
    val cashOutStatus: Int,
    val cashOutTime: String,
    val checkTime: String,
    val fee: Double,
    val id: Int,
    val receiptNo: String,
    val receiptType: Int,
    val remark: String
) : IIncomeDetailEntity {
    override fun mainRes(): Int = R.mipmap.icon_withdraw_record_alipay_main

    override fun getTotalStr(): String = cashOutPrice.toString()

    override fun getTag(): String =
        StringUtils.getStringArray(R.array.withdraw_status_arr)[cashOutStatus]

    override fun getInfoList(): ArrayList<IncomeDetailInfo> =
        arrayListOf<IncomeDetailInfo>().apply {
            add(IncomeDetailInfo(StringUtils.getString(R.string.cash_out_time), cashOutTime))
            if (checkTime.isNotEmpty()) {
                add(IncomeDetailInfo(StringUtils.getString(R.string.verify_time), checkTime))
            }
            add(
                IncomeDetailInfo(
                    StringUtils.getString(R.string.to_account_of_way),
                    StringUtils.getStringArray(R.array.withdraw_type_arr)[receiptType]
                )
            )
            if (receiptNo.isNotEmpty()) {
                add(IncomeDetailInfo(StringUtils.getString(R.string.to_account_account), receiptNo))
            }
            if (remark.isNotEmpty()) {
                add(IncomeDetailInfo(StringUtils.getString(R.string.failure_reason), remark))
            }
        }

    override fun canGoOrderDetail(): Boolean = false
}