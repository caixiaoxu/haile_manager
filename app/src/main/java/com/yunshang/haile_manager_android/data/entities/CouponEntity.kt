package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/12 15:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CouponEntity(
    var couponType: Int? = null,
    var endAt: String? = null,
    var goodsCategoryIds: List<Int>? = null,
    var maxDiscountPrice: Double? = null,
    var orderReachPrice: Double? = null,
    var organizationType: Int? = null,
    var percentage: Double? = null,
    var reduce: Double? = null,
    var shopIds: List<Int>? = null,
    var specifiedPrice: Double? = null,
    var userCouponCountList: List<UserCouponCount> = listOf(UserCouponCount())
) : BaseObservable() {

    @get:Bindable
    var issueCouponsUser: String
        get() = userCouponCountList.firstOrNull()?.phone ?: ""
        set(value) {
            userCouponCountList.firstOrNull()?.let {
                it.phone = value
            } ?: run {
                userCouponCountList = listOf(
                    UserCouponCount().apply { phone = value }
                )
            }
            notifyPropertyChanged(BR.issueCouponsUser)
        }

    @get:Bindable
    val couponTypeName: String
        get() = couponType?.let {
            StringUtils.getStringArray(R.array.coupon_type)[it]
        } ?: ""

    @get:Bindable
    var couponTypeVal: Int
        get() = couponType ?: 0
        set(value) {
            couponType = value
            notifyPropertyChanged(BR.couponTypeVal)
            notifyPropertyChanged(BR.couponTypeName)
        }

    @Transient
    @get:Bindable
    var reduceVal: String = ""
        get() = if (field.isNullOrEmpty()) reduce?.toString() ?: "" else field
        set(value) {
            try {
                field = value
                reduce = if (value.isEmpty()) null else value.toDouble()
                notifyPropertyChanged(BR.reduceVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @Transient
    @get:Bindable
    var percentageVal: String = ""
        get() = if (field.isNullOrEmpty()) percentage?.toString() ?: "" else field
        set(value) {
            try {
                field = value
                percentage = if (value.isEmpty()) null else value.toDouble()
                notifyPropertyChanged(BR.percentageVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @Transient
    @get:Bindable
    var maxDiscountPriceVal: String = ""
        get() = if (field.isNullOrEmpty()) maxDiscountPrice?.toString() ?: "" else field
        set(value) {
            try {
                field = value
                maxDiscountPrice = if (value.isEmpty()) null else value.toDouble()
                notifyPropertyChanged(BR.maxDiscountPriceVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @Transient
    @get:Bindable
    var orderReachPriceVal: String = ""
        get() = if (field.isNullOrEmpty()) orderReachPrice?.toString() ?: "" else field
        set(value) {
            try {
                field = value
                orderReachPrice = if (value.isEmpty()) null else value.toDouble()
                notifyPropertyChanged(BR.orderReachPriceVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @get:Bindable
    var issueCouponsNum: String
        get() = userCouponCountList.firstOrNull()?.count?.toString() ?: ""
        set(value) {
            try {
                val num = value.toInt()
                userCouponCountList.firstOrNull()?.let {
                    it.count = num
                } ?: run {
                    userCouponCountList = listOf(
                        UserCouponCount().apply { count = num }
                    )
                }
                notifyPropertyChanged(BR.issueCouponsNum)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @get:Bindable
    var validityVal: String
        get() = endAt ?: ""
        set(value) {
            endAt = value
            notifyPropertyChanged(BR.validityVal)
        }
}

data class UserCouponCount(
    var count: Int? = null,
    var phone: String? = null,
    var userId: Int? = null
)