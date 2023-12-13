package com.yunshang.haile_manager_android.data.entities

import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.extend.formatMoney
import com.yunshang.haile_manager_android.utils.DateTimeUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/13 16:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CouponEntity(
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
    val lastEditor: String,
    val maxDiscountPrice: String,
    val orderReachPrice: String,
    val orgIds: List<Int>,
    val organizationType: Int,
    val percentage: String,
    val phone: String?,
    val promotionId: Int,
    val reduce: String,
    val shopIds: List<Int>,
    val shopNames: List<String>,
    val shopVOs: List<CouponShopVO>?,
    val specifiedPrice: Double,
    val startAt: String,
    val state: Int,
    val subjectId: Int,
    val subjectTitle: String,
    val usedReduce: String,
    val usedShopName: String,
    val usedTime: String,
    val usedTradeNo: String,
    val value: String
) : BaseObservable() {
    @Transient
    @get:Bindable
    var selected:Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    val stateVal: String
        get() = when (state) {
            1 -> "未使用"
            30 -> "已使用"
            31 -> "已过期"
            32 -> "已作废"
            else -> ""
        }

    val stateColor: Int
        get() = ContextCompat.getColor(
            Constants.APP_CONTEXT, when (state) {
                1 -> R.color.color_FFA936
                32 -> R.color.color_F7612F
                else -> R.color.color_black_85
            }
        )

    val title: String
        get() = "${StringUtils.getStringArray(R.array.coupon_type)[couponType]} ${
            if (3 == couponType) percentage + "折" else if (4 == couponType) {
                if (0.0 == specifiedPrice) "免费" else "${specifiedPrice.formatMoney()}元"
            } else reduce + "元"
        }"

    val user: String
        get() = StringUtils.getString(R.string.user_phone) + "：" + (phone ?: "")

    val validity: String
        get() = StringUtils.getString(R.string.validity_to) + "：" + DateTimeUtils.formatDateTimeForStr(
            startAt,
            "yyyy-MM-dd"
        ) + "至" + DateTimeUtils.formatDateTimeForStr(endAt, "yyyy-MM-dd")
}

data class CouponShopVO(
    val operatorPhone: String,
    val shopAddress: String,
    val shopId: Int,
    val shopName: String
)