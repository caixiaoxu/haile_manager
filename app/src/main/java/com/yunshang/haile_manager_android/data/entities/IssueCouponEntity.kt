package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

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
data class IssueCouponEntity(
    var couponType: Int = 1,
    var endAt: String = DateTimeUtils.formatDateTimeEndParam(DateTimeUtils.addDay(Date(), 1)),
    var goodsCategoryIds: List<Int>? = listOf(0),
    var maxDiscountPrice: Double? = null,
    var orderReachPrice: Double? = null,
    var organizationType: Int = 1,
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
        get() = couponType.let {
            StringUtils.getStringArray(R.array.coupon_type)[it]
        } ?: ""

    @get:Bindable
    var couponTypeVal: Int
        get() = couponType
        set(value) {
            couponType = value
            notifyPropertyChanged(BR.couponTypeVal)
            notifyPropertyChanged(BR.couponTypeName)
        }

    @Transient
    @get:Bindable
    var reduceVal: String = ""
        get() = field.ifEmpty { reduce?.toString() ?: "" }
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
    var specifiedPriceVal: String = ""
        get() = field.ifEmpty { specifiedPrice?.toString() ?: "" }
        set(value) {
            try {
                field = value
                specifiedPrice = if (value.isEmpty()) null else value.toDouble()
                notifyPropertyChanged(BR.specifiedPriceVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @Transient
    @get:Bindable
    var percentageVal: String = ""
        get() = field.ifEmpty { percentage?.toString() ?: "" }
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
        get() = field.ifEmpty { maxDiscountPrice?.toString() ?: "" }
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
        get() = field.ifEmpty { orderReachPrice?.toString() ?: "" }
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
                val num = if (value.isEmpty()) null else value.toInt()
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
        get() = DateTimeUtils.formatDateTimeForStr(endAt, "yyyy-MM-dd")
        set(value) {
            endAt = value
            notifyPropertyChanged(BR.validityVal)
        }

    @Transient
    var shopNameVal: String = ""
        get() = if (1 == organizationType) StringUtils.getString(R.string.all_shop) else field
        set(value) {
            field = value
            notifyPropertyChanged(BR.validityVal)
        }

    @Transient
    var categoryNameVal: String = ""
        get() = if (null == goodsCategoryIds) "" else if (goodsCategoryIds?.contains(0) == true) StringUtils.getString(
            R.string.all_device
        ) else field
        set(value) {
            field = value
            notifyPropertyChanged(BR.validityVal)
        }
}

data class UserCouponCount(
    var count: Int? = null,
    var phone: String? = null,
    var userId: Int? = null
)