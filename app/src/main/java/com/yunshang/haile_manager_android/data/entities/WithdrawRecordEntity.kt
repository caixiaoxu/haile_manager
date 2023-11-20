package com.yunshang.haile_manager_android.data.entities

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/13 15:31
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class WithdrawRecordEntity(
    val cashOutPrice: Double,
    val cashOutStatus: Int,
    val cashOutTime: String,
    val cashOutType: Int,
    val checkTime: String,
    val id: Int,
    val feeAmount: Double? = null,
    val totalAmount: Double? = null,
    val icon: String? = null,
) : IMultiTypeEntity {

    val recordAmount: SpannableString
        get() = "${com.lsy.framelib.utils.StringUtils.getString(R.string.cash_out_amount)}：".let { prefix ->
            StringUtils.formatMultiStyleStr(
                "$prefix$cashOutPrice",
                arrayOf(
                    StyleSpan(Typeface.NORMAL),
                ), 0, prefix.length
            )
        }

    val feeAmountVal: SpannableString
        get() = "${com.lsy.framelib.utils.StringUtils.getString(R.string.service_charge)}：".let { prefix ->
            StringUtils.formatMultiStyleStr(
                "$prefix$feeAmount",
                arrayOf(
                    StyleSpan(Typeface.NORMAL),
                ), 0, prefix.length
            )
        }

    val arrivalAmount: SpannableString
        get() = "${com.lsy.framelib.utils.StringUtils.getString(R.string.arrival_amount)}：".let { prefix ->
            StringUtils.formatMultiStyleStr(
                "$prefix$cashOutPrice",
                arrayOf(
                    StyleSpan(Typeface.NORMAL),
                ), 0, prefix.length
            )
        }

    fun typeVal(): String =
        com.lsy.framelib.utils.StringUtils.getStringArray(R.array.withdraw_type_arr)[cashOutType] +
                com.lsy.framelib.utils.StringUtils.getString(R.string.withdraw)

    fun statusVal(): String =
        com.lsy.framelib.utils.StringUtils.getStringArray(R.array.withdraw_status_arr)[cashOutStatus]

    override fun getMultiType(): Int = when (cashOutStatus) {//0.提交申请, 1.提现中 2.提现成功 3. 提现失败 4.审核不通过
        2 -> 1
        3, 4 -> 2
        else -> 0
    }

    override fun getMultiTypeBgRes(): IntArray = intArrayOf()

    override fun getMultiTypeTxtColors(): IntArray = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.color_FFA936
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.common_txt_color
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.hint_color
        ),
    )
}