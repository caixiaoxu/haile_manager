package com.yunshang.haile_manager_android.data.entities

import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.extend.formatMoney
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 14:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class IncomeExpensesDetailEntity(
    val amount: Double,
    val businessTime: String,
    val categoryCode: String,
    val categoryName: String,
    val orderId: Int,
    val orderNo: String,
    val shopId: Int,
    val shopName: String,
    val transactionType: Int
) : IMultiTypeEntity {
    fun amountVal() = (if (1 == transactionType) amount else -amount).formatMoney(true)
    fun getOrderTime() =
        StringUtils.getString(if (1 == transactionType) R.string.pay_time else R.string.refund_time) + "：" + businessTime

    fun transactionTypeVal() =
        StringUtils.getString(if (1 == transactionType) R.string.earning else R.string.expend)

    override fun getMultiType(): Int = if (transactionType == 1) 0 else 1

    override fun getMultiTypeBgRes(): IntArray = intArrayOf(
        R.drawable.shape_sf7f7f7_r10,
        R.drawable.shape_s2630c19a_r10,
    )

    override fun getMultiTypeTxtColors(): IntArray = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.common_txt_color
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.color_green
        ),
    )
}