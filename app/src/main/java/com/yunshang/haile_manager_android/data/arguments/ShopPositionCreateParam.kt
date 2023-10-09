package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/9 15:21
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopPositionCreateParam(
    var name: String? = null,//名称
    var shopId: Int? = null,//门店Id
    var area: String? = null,//地区
    var address: String? = null,//地址
    var lat: Int? = null,
    var lng: Int? = null,
    var workTime: String? = null,
    var workTimeStr: String? = null,
    var serviceTelephone: String? = null,
    var sex: Int? = null
) : BaseObservable() {

    @Transient
    @get:Bindable
    var shopName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.shopName)
        }

    @Transient
    @get:Bindable
    var location: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.location)
        }

    @get:Bindable
    var addressVal: String?
        get() = address
        set(value) {
            address = value
            notifyPropertyChanged(BR.addressVal)
        }

    var sexVal: Int?
        get() = sex
        set(value) {
            sex = value
            notifyPropertyChanged(BR.sexNameVal)
        }

    @get:Bindable
    var sexNameVal: String = ""
        get() = when (sex) {
            0 -> "男"
            1 -> "女"
            else -> "不限"
        }

    @Transient
    @get:Bindable
    var workTimeValue: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.workTimeValue)
        }

    /**
     * 切换营业时间
     */
    fun changeWorkTime(
        timeList: MutableList<BusinessHourEntity>? = null,
        workTime: String? = null
    ) {
        timeList?.let {
            workTimeStr = GsonUtils.any2Json(timeList.map { item ->
                BusinessHourParams(
                    item._weekDays.map { day -> day.id },
                    item._workTime.split(" ")[0]
                )
            })
            workTimeValue = timeList.joinToString("\n") { item ->
                item.hourWeekVal + item.workTime
            }
            this.workTime = workTime ?: run {
                val arr = Array(7) { "" }
                for (index in 0..6) {
                    arr[index] =
                        timeList.filter { item -> null != item.weekDays.find { day -> day.id == (index + 1) } }
                            .joinToString(",") { item ->
                                item.workTime.split(" ")[0]
                            }
                }
                GsonUtils.any2Json(arr)
            }
        }
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