package com.shangyun.haile_manager_android.data.entities

import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.rule.IMultiTypeEntity
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import com.shangyun.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/29 14:49
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class IncomeListByDayEntity(
    val amount: Double,
    val category: String,
    val createTime: String,
    val id: Int,
    val orderNo: String,
    val orderType: String
) : IMultiTypeEntity {

    fun getInComeListDateTime() = DateTimeUtils.formatDateTimeForStr(createTime, "MM/dd HH:mm:ss")

    fun getAmountStr() = StringUtils.formatNumberStr(amount)

    override fun getMultiType(): Int = if (amount >= 0) 0 else 1

    override fun getMultiTypeBgRes(): IntArray = intArrayOf(
        R.drawable.shape_s26f0a258_r4,
        R.drawable.shape_s2630c19a_r4,
    )

    override fun getMultiTypeTxtColors(): IntArray = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.colorPrimary
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.color_green
        ),
    )
}