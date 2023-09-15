package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.utils.DateTimeUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/14 18:12
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CouponDetailEntity(
    val accountId: Int,
    val assetNo: String,
    val assetSource: Int,
    val bizTypeId: Int,
    val bizTypeName: String,
    val couponType: Int,
    val createTime: String,
    val endAt: String,
    val goodsCategoryIds: List<Int>,
    val goodsCategoryNames: List<String>,
    val hourMinuteEndTime: String,
    val hourMinuteStartTime: String,
    val id: Int,
    val invalidTime: String,
    val invalidUserId: Int,
    val invalidUserAccount: String,
    val invalidUserName: String,
    val lastEditor: String,
    val maxDiscountPrice: String,
    val orderReachPrice: String,
    val orgIds: List<Any>,
    val organizationType: Int,
    val percentage: String,
    val phone: String,
    val promotionId: Int,
    val reduce: String,
    val shopIds: List<Any>,
    val shopNames: List<String>,
    val shopVOs: List<CouponShopVO>,
    val specifiedPrice: String,
    val startAt: String,
    val state: Int,
    val subjectId: Int,
    val subjectTitle: String,
    val usedReduce: String,
    val usedShopName: String,
    val usedTime: String,
    val usedTradeNo: String,
    val value: String,
    val createUserAccount: String,
    val createUserId: Int,
    val createUserName: String
) {
    val stateVal: String
        get() = when (state) {
            1 -> "未使用"
            30 -> "已使用"
            31 -> "已过期"
            32 -> "已作废"
            else -> ""
        }

    val couponTypeVal: String
        get() = StringUtils.getStringArray(R.array.coupon_type)[couponType]

    val reduceVal: String
        get() = reduce + StringUtils.getString(R.string.unit_yuan)

    val percentageVal: String
        get() = percentage + StringUtils.getString(R.string.unit_folds)

    val specifiedPriceVal: String
        get() = specifiedPrice + StringUtils.getString(R.string.unit_yuan)

    val maxDiscountPriceVal: String
        get() = maxDiscountPrice + StringUtils.getString(R.string.unit_yuan)

    val orderReachPriceVal: String
        get() = StringUtils.getString(R.string.coupon_condition_value, orderReachPrice)

    val indateVal: String
        get() = DateTimeUtils.formatDateTimeForStr(
            startAt,
            "yyyy-MM-dd"
        ) + "至" + DateTimeUtils.formatDateTimeForStr(endAt, "yyyy-MM-dd")

    val shopVal: String
        get() = if (1 == organizationType) StringUtils.getString(R.string.all_shop)
        else shopNames.joinToString("、")

    val categoryVal: String
        get() = if (goodsCategoryIds.contains(0)) StringUtils.getString(R.string.all_device)
        else goodsCategoryNames.joinToString("、")

    val userVal: String
        get() = "$createUserName $createUserAccount"

    val invalidUserNameVal: String
        get() = "$invalidUserName $invalidUserAccount"
}