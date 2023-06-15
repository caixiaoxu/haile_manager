package com.yunshang.haile_manager_android.data.entities

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/15 14:11
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SchemeConfigsDetailEntity(
    val configName: String,
    val createTime: String,
    val creatorName: String,
    val discountProportion: Long,
    val exchangeRate: Int,
    val goodsId: Int,
    val id: Int,
    val isAllowRefund: Int,
    val isDefaultConfig: Boolean,
    val isForceUse: Int,
    val maxRewardNum: Int,
    val operatorId: Int,
    val rewardsConfig: List<RewardsConfig>,
    val shopId: Int,
    val suspendFlag: Boolean
) : IMultiTypeEntity {

    fun statusVal(): String =
        StringUtils.getString(if (suspendFlag) R.string.out_of_service else R.string.enabled)

    override fun getMultiType(): Int = if (suspendFlag) 1 else 0

    override fun getMultiTypeBgRes() = intArrayOf(
        R.drawable.shape_strokef0a258_solid26f0a258_r4,
        R.drawable.shape_sf7f7f7_r4,
    )

    override fun getMultiTypeTxtColors() = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.colorPrimary
        ),
        Color.parseColor("#999999")
    )
}

data class RewardsConfig(
    var cashValue: Double? = null,
    val goodsId: Int? = null,
    val id: Int? = null,
    val itemId: Int? = null,
    val price: Int? = null,
    var reach: Int? = null,
    var reward: Int? = null,
    val shopId: Int? = null,
    val shopTokenCoinId: Int? = null,
    var status: Int? = null
) {
    fun switchSchemeOpen(isCheck: Boolean) {
        status = if (isCheck) 0 else 1
    }
}