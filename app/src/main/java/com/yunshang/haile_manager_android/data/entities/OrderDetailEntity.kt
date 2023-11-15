package com.yunshang.haile_manager_android.data.entities

import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/9 17:59
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class OrderDetailEntity(
    val buyerId: Int,
    val buyerName: String,
    val buyerPhone: String,
    @SerializedName("canCompensate")
    var _canCompensate: Boolean,
    @SerializedName("canRefund")
    var _canRefund: Boolean,
    val canReset: Boolean,
    val canStart: Boolean,
    @SerializedName("createTime")
    val _createTime: String,
    val deviceName: String,
    val deviceSubType: String,
    val deviceType: String,
    val discountPrice: Double,
    val goodsCategoryIds: ArrayList<Int>,
    val id: Int,
    val orderCategory: Int,
    val orderNo: String,
    val orderSubCategory: Int,
    val orderSubType: Int,
    val orderType: Int,
    val originPrice: Double,
    val payMethod: String,
    val payMethodType: Int,
    val payNo: String,
    val payPrice: Double,
    @SerializedName("payTime")
    val _payTime: String,
    val promotionList: List<Promotion>,
    val realPrice: Double,
    val refundBy: String,
    val refundDesc: String,
    val refundNo: String,
    val refundPrice: Double,
    @SerializedName("refundTime")
    val _refundTime: String,
    val shopId: Int,
    val shopName: String? = null,
    val skuList: List<Sku>,
    val state: Int,
    val stateDesc: String,
    val appointmentInfo: AppointmentInfo?,
    val appointmentTime: String,
    val canCancelReserve: Boolean,
    val endState: Int,
    val endStateDesc: String?,
    val completeTime: String? = null,
    val positionId: Int? = null,
    val positionName: String? = null,
    val code: String? = null,
    val refundTag: String? = null,
) : BaseObservable() {
    val createTime: String
        get() = DateTimeUtils.formatDateTimeForStr(_createTime, "yyyy-MM-dd HH:mm")
    val appointTime: String
        get() = appointmentInfo?.let {
            DateTimeUtils.formatDateTimeForStr(
                appointmentInfo.appointmentUsageTime,
                "yyyy-MM-dd HH:mm"
            )
        } ?: ""
    val refundTime: String
        get() = DateTimeUtils.formatDateTimeForStr(_refundTime, "yyyy-MM-dd HH:mm")
    val payTime: String
        get() = DateTimeUtils.formatDateTimeForStr(_payTime, "yyyy-MM-dd HH:mm")

    @get:Bindable
    var canRefund: Boolean
        get() = _canRefund
        set(value) {
            _canRefund = value
            notifyPropertyChanged(BR.canRefund)
        }

    @get:Bindable
    var canCompensate: Boolean
        get() = _canCompensate
        set(value) {
            _canCompensate = value
            notifyPropertyChanged(BR.canCompensate)
        }

    val deviceTypeVal: String
        get() = skuList.firstOrNull()?.deviceType ?: ""

    val skuDeviceTypes: List<Sku>
        get() {
            val list = hashMapOf<Int,Sku>()
            skuList.forEach {
                if (null == list[it.goodsId]){
                    list[it.goodsId] = it
                }
            }
            return skuList

//            return list.values.joinToString("\n"){item->
//                val goodName = item.goodsName
//                val goodId = "（设备编号：${item.goodsId}）"
//                goodName + goodId
//            }
        }

    fun endStateVal(): String = when (endState) {
        1050 -> endStateDesc?.let { "故障结束-$it" } ?: ""
        1000 -> "正常结束"
        else -> ""
    }

    val hasRefundMoney: Boolean
        get() = refundTag?.split(",")?.contains("1") ?: false

    val hasRefundCoupon: Boolean
        get() = refundTag?.split(",")?.contains("2") ?: false


    val shopPositionName: String
        get() = if (!shopName.isNullOrEmpty() && !positionName.isNullOrEmpty()) "$shopName-$positionName"
        else if (!shopName.isNullOrEmpty()) shopName
        else if (!positionName.isNullOrEmpty()) positionName
        else ""
}

data class AppointmentInfo(
    val appointmentReason: String,
    val appointmentState: Int,
    val appointmentTime: String,
    val appointmentUsageTime: String
)