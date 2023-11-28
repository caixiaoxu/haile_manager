package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/21 18:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class AppointmentSettingEntity(
    var settingList: MutableList<SettingItem>? = null,
    var autoRefund: Int? = 1,
    var shopId: Int? = null,
    var checkTime: Int? = 10,
    var reserveMethod: Int? = 2
) : BaseObservable() {
    var shopIdList: IntArray? = null
    var settings: MutableList<SettingItem>? = null

    @get:Bindable
    var autoRefundVal: Boolean
        get() = 1 == autoRefund
        set(value) {
            autoRefund = if (value) 1 else 0
            notifyPropertyChanged(BR.autoRefundVal)
        }

    @Transient
    @get:Bindable
    var checkTimeVal: String = ""
        get() = field.ifEmpty { checkTime.toString() }
        set(value) {
            field = value
            try {
                checkTime = value.toInt()
            } catch (_: Exception) {
            }
            notifyPropertyChanged(BR.reserveMethod1Val)
        }

    @get:Bindable
    var reserveMethod1Val: Boolean
        get() = 1 == reserveMethod
        set(value) {
            reserveMethod = if (value) 1 else 2
            notifyPropertyChanged(BR.reserveMethod1Val)
            notifyPropertyChanged(BR.reserveMethod2Val)
        }

    @get:Bindable
    var reserveMethod2Val: Boolean
        get() = 2 == reserveMethod
        set(value) {
            reserveMethod = if (value) 2 else 1
            notifyPropertyChanged(BR.reserveMethod1Val)
            notifyPropertyChanged(BR.reserveMethod2Val)
        }

    fun showSettingList(): Boolean =
        settingList?.any { item -> item.appointSwitchVal } ?: false || autoRefundVal

    fun showContextList(): String {
        val sb = StringBuilder()

        val tokenCoinList =
            settingList?.filter { item -> item.appointSwitchVal && !item.goodsCategoryName.isNullOrEmpty() }
        if (!tokenCoinList.isNullOrEmpty()) {
            sb.append("\n预约设备类型：${tokenCoinList.joinToString("、") { item -> item.goodsCategoryName ?: "" }}")
        }
        sb.append("\n预约验证时间：${checkTime}分钟")
        if (1 == reserveMethod) {
            sb.append("\n预约后付费")
        } else if (2 == reserveMethod) {
            sb.append("\n预约先付费")
            if (autoRefundVal) {
                sb.append("\n预约不使用自动退款")
            }
        }
        return if (sb.isNotEmpty()) sb.substring(1) else ""
    }
}

data class SettingItem(
    var appointSwitch: Int? = null,
    val goodsCategoryId: Int? = null,
    val goodsCategoryName: String? = null
) : BaseObservable() {

    @get:Bindable
    var appointSwitchVal: Boolean
        get() = 1 == appointSwitch
        set(value) {
            appointSwitch = if (value) 1 else 0
            notifyPropertyChanged(BR.appointSwitchVal)
        }
}