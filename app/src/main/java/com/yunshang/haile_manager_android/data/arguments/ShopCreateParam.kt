package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.data.entities.ShopPaySettingsEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/14 15:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopCreateParam(
    var id: Int? = null,
    var shopType: Int = -1,
    var name: String = "",
    var provinceId: Int = -1,
    var cityId: Int = -1,
    var districtId: Int = -1,
    var area: String = "",
    var address: String = "",
    var lng: Double? = null,
    var lat: Double? = null,
    var schoolName: String = "",
    var workTime: String = "",
    var workTimeStr: String = "",
    var serviceTelephone: String = "",
    var shopBusiness: String = "",
    var schoolId: Int = -1,
    @SerializedName("paymentSettings")
    var _paymentSettings: ShopPaySettingsEntity? = null,
    var operationSettings: OperationSettings = OperationSettings(0)
) : BaseObservable() {

    @get:Bindable
    var areaVal: String
        get() = area
        set(value) {
            area = value
            notifyPropertyChanged(BR.areaVal)
        }

    @get:Bindable
    var paymentSettings: ShopPaySettingsEntity?
        get() = _paymentSettings
        set(value) {
            _paymentSettings = value
            notifyPropertyChanged(BR.paymentSettings)
        }
}

data class BusinessHourEntity(
    var _weekDays: List<ActiveDayParam> = listOf(),
    var _workTime: String = ""
) : BaseObservable() {

    @get:Bindable
    var weekDays: List<ActiveDayParam>
        get() = _weekDays
        set(value) {
            _weekDays = value.sortedBy { item -> item.id }
            notifyPropertyChanged(BR.hourWeekVal)
        }

    @get:Bindable
    val hourWeekVal: String
        get() = if (_weekDays.isEmpty()) "" else {
            val first = _weekDays.first()
            val last = _weekDays.last()
            val temp = last.id - first.id
            if (_weekDays.size == (temp + 1) && temp > 2) {
                "${first.name}至${last.name}"
            } else
                _weekDays.joinToString("、") { item -> item.name }
        }

    @get:Bindable
    var workTime: String
        get() = _workTime
        set(value) {
            _workTime = value
            notifyPropertyChanged(BR.workTime)
        }

    fun isEmpty(): Boolean = _weekDays.isEmpty() && _workTime.isEmpty()

    fun isNotFull(): Boolean = !isEmpty() && (_weekDays.isEmpty() || _workTime.isEmpty())

    fun formatData(days: List<Int>, time: String) {
        _weekDays = ShopParam.businessDay.filter { item -> item.id in days }
        _workTime = time
    }
}

data class BusinessHourParams(
    val weekDays: List<Int>,
    val workTime: String
)

data class OperationSettings(
    val volumeVisibleState: Int
)