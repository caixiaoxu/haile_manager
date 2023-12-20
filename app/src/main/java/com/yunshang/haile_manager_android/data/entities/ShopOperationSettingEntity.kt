package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/31 14:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopOperationSettingEntity(
    val paymentSetting: ShopPaySettingsEntity? = null,
    val compensationSetting: OperationCompensationSetting? = null,
    val appointSetting: AppointmentSettingEntity? = null,
    val operationSetting: OperationFlowSetting? = null,
    var nearOrderSettingDetailDTO: NearOrderSettings? = null,
    var freeSelfClearSettingDetailDTO: FreeSelfClearSettings? = null,
) : BaseObservable() {
    var nearOrderSettingsForm: NearOrderSettings? = null
    var freeSelfClearSettingsForm: FreeSelfClearSettings? = null

    @Transient
    @get:Bindable
    var showItem1: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem1)
        }

    @Transient
    @get:Bindable
    var showItem1Content: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem1Content)
        }

    @Transient
    @get:Bindable
    var showItem2: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem2)
        }

    @Transient
    @get:Bindable
    var showItem2Content: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem2Content)
        }

    @Transient
    @get:Bindable
    var showItem3: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem3)
        }

    @Transient
    @get:Bindable
    var showItem3Content: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem3Content)
        }

    @Transient
    @get:Bindable
    var showItem4: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem4)
        }

    @Transient
    @get:Bindable
    var showItem4Content: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem4Content)
        }

    @Transient
    @get:Bindable
    var showItem5: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem5)
        }

    @Transient
    @get:Bindable
    var showItem5Content: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem5Content)
        }

    @Transient
    @get:Bindable
    var showItem6: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem6)
        }

    @Transient
    @get:Bindable
    var showItem6Content: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showItem6Content)
        }
}

data class OperationCompensationSetting(
    var autoCompensateCoupon: Int? = 1,
    var autoRefundMoney: Int? = 1,
    val shopId: Int? = null
) : BaseObservable() {
    var shopIdList: IntArray? = null

    fun showCompensation(): Boolean = autoRefundMoneyVal || autoCompensateCouponVal

    @get:Bindable
    var autoRefundMoneyVal: Boolean
        get() = 1 == autoRefundMoney
        set(value) {
            autoRefundMoney = if (value) {
                autoCompensateCouponVal = false
                1
            } else 0
            notifyPropertyChanged(BR.autoRefundMoneyVal)
        }

    @get:Bindable
    var autoCompensateCouponVal: Boolean
        get() = 1 == autoCompensateCoupon
        set(value) {
            autoCompensateCoupon = if (value) {
                autoRefundMoneyVal = false
                1
            } else 0
            notifyPropertyChanged(BR.autoCompensateCouponVal)
        }

    fun showCompensationContent(): String =
        if (autoRefundMoneyVal) StringUtils.getString(R.string.compensation_setting_name2)
        else if (autoCompensateCouponVal) StringUtils.getString(R.string.compensation_setting_name1)
        else ""
}

data class OperationFlowSetting(
    val shopId: Int? = null,
    var volumeVisibleState: Int? = 1
) : BaseObservable() {
    var shopIdList: IntArray? = null

    @get:Bindable
    var volumeVisibleStateVal: Boolean
        get() = 1 == volumeVisibleState
        set(value) {
            volumeVisibleState = if (value) 1 else 0
            notifyPropertyChanged(BR.volumeVisibleStateVal)
        }
}

data class NearOrderSettings(
    val shopId: Int? = null,
    var volumeVisibleState: Int? = null
): BaseObservable() {
    var shopIdList: IntArray? = null

    @get:Bindable
    var volumeVisibleStateVal: Boolean
        get() = 1 == volumeVisibleState
        set(value) {
            volumeVisibleState = if (value) 1 else 0
            notifyPropertyChanged(BR.volumeVisibleStateVal)
        }
}

data class FreeSelfClearSettings(
    var shopId: Int? = null,
    var volumeVisibleState: Int? = 1
) : BaseObservable() {
    var shopIdList: IntArray? = null

    @get:Bindable
    var volumeVisibleStateVal: Boolean
        get() = 1 == volumeVisibleState
        set(value) {
            volumeVisibleState = if (value) 1 else 0
            notifyPropertyChanged(BR.volumeVisibleStateVal)
        }
}