package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

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
data class ShopEntity(
    val id: Int,
    val name: String,
    val income: Double,
    val deviceNum: Int,

    val positionCount: Int,// 点位
): BaseObservable() {
    @Transient
    var page = 1
    @Transient
    var _positionList: MutableList<ShopPositionEntity>? = null

    init {
        page = 1
        _positionList = mutableListOf()
    }

    @get:Bindable
    var positionList: MutableList<ShopPositionEntity>
        get() = _positionList ?: mutableListOf()
        set(value) {
            _positionList = value
            notifyPropertyChanged(BR.positionList)
        }
}