package com.yunshang.haile_manager_android.data.entities

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
    val id: Int
) : IMultiTypeEntity {

    fun getAmount(): String = StringUtils.formatNumberStr(-cashOutPrice)

    fun typeVal(): String =
        com.lsy.framelib.utils.StringUtils.getStringArray(R.array.withdraw_type_arr)[cashOutType] +
                com.lsy.framelib.utils.StringUtils.getString(R.string.withdraw)

    fun statusVal(): String =
        com.lsy.framelib.utils.StringUtils.getStringArray(R.array.withdraw_status_arr)[cashOutStatus]

    override fun getMultiType(): Int = when (cashOutStatus) {
        2 -> 1
        3, 4 -> 2
        else -> 0
    }

    override fun getMultiTypeBgRes(): IntArray = intArrayOf(0)

    override fun getMultiTypeTxtColors(): IntArray = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.common_sub_txt_color
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.color_green
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.hint_color
        ),
    )
}