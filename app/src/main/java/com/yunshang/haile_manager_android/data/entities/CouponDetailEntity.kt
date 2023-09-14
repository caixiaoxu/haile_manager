package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R

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
    val goodsCategoryIds: List<Any>,
    val goodsCategoryNames: List<Any>,
    val hourMinuteEndTime: String,
    val hourMinuteStartTime: String,
    val id: Int,
    val invalidTime: String,
    val invalidUserId: Int,
    val invalidUserName: String,
    val lastEditor: String,
    val maxDiscountPrice: Int,
    val orderReachPrice: Int,
    val orgIds: List<Any>,
    val organizationType: Int,
    val percentage: Int,
    val phone: String,
    val promotionId: Int,
    val reduce: Int,
    val shopIds: List<Any>,
    val shopNames: List<Any>,
    val shopVOs: List<CouponShopVO>,
    val specifiedPrice: Int,
    val startAt: String,
    val state: Int,
    val subjectId: Int,
    val subjectTitle: String,
    val usedReduce: Int,
    val usedShopName: String,
    val usedTime: String,
    val usedTradeNo: String,
    val value: Int
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
}