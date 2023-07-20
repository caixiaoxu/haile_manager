package com.yunshang.haile_manager_android.data.entities

import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/15 11:49
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HaixinSchemeConfigEntity(
    val balanceLimit: String,
    val configName: String,
    val createdAt: String,
    val deleteFlag: Boolean,
    val discountProportion: String,
    val exchangeRate: Int,
    val id: Int,
    val isAllowRefund: Int,
    val isForceUse: Int,
    val memberNum: Int,
    val operatorId: Int,
    val shopId: Int,
    val shopName: String,
    val suspendFlag: Boolean
) : IMultiTypeEntity {

    fun statusVal(): Int = if (suspendFlag) R.string.out_of_service else R.string.enabled

    override fun getMultiType(): Int = if (suspendFlag) 1 else 0

    override fun getMultiTypeBgRes() = intArrayOf(
        R.drawable.shape_s26f0a258_r4,
        R.drawable.shape_sf7f7f7_r4,
    )

    override fun getMultiTypeTxtColors() = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.colorPrimary
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.common_sub_txt_color
        ),
    )

}