package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IIncomeDetailEntity
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/30 10:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class IncomeDetailEntity(
    val amount: Double,
    val category: String,
    val createTime: String,
    val id: Int,
    val orderId: Int,
    val orderNo: String,
    val orderType: String,
    val payTime: String,
    val payType: String,
    val settlementTime: String
) : IIncomeDetailEntity {
    override fun isPlusOrMinus(): Boolean = amount >= 0

    override fun getTotalStr(): String = StringUtils.formatNumberStr(amount) ?: ""

    override fun getTag(): String = orderType

    override fun getInfoList(): ArrayList<IncomeDetailInfo> = arrayListOf(
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.order_no),
            orderNo,
            true
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.create_time),
            DateTimeUtils.formatDateTimeForStr(createTime, "yyyy/MM/dd HH:mm:ss"),
            false
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.pay_time),
            DateTimeUtils.formatDateTimeForStr(payTime, "yyyy/MM/dd HH:mm:ss"),
            false
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.settlement_time),
            DateTimeUtils.formatDateTimeForStr(settlementTime, "yyyy/MM/dd HH:mm:ss"),
            false
        ),
        IncomeDetailInfo(
            com.lsy.framelib.utils.StringUtils.getString(R.string.model_of_payment),
            payType,
            false
        ),
    )

    override fun canGoOrderDetail(): Boolean = true
}