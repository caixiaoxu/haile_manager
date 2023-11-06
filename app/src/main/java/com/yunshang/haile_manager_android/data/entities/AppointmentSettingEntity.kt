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
    var autoRefund: Int? = null,
    var shopId: Int? = null
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

    fun showSettingList(): Boolean = settingList?.any { item -> item.appointSwitchVal } ?: false || autoRefundVal

    fun showContextList(): String {
        val sb = StringBuilder()

        val tokenCoinList =
            settingList?.filter { item -> item.appointSwitchVal && !item.goodsCategoryName.isNullOrEmpty() }
        if (!tokenCoinList.isNullOrEmpty()) {
            sb.append("\n预约设备类型：${tokenCoinList.joinToString("、") { item -> item.goodsCategoryName ?: "" }}")
        }

        if (autoRefundVal) {
            sb.append("\n预约不使用自动退款")
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