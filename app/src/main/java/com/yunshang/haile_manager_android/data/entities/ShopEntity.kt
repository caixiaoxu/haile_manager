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
    val id: Int? = null,
    val name: String? = null,
    val income: Double? = null,
    val deviceNum: Int? = null,
    val shopTypeName: String? = null,
    val positionCount: Int? = null,// 点位
) : BaseObservable() {

    // 点位列表
    @Transient
    var _positionList: MutableList<ShopPositionEntity>? = null

    @Transient
    var _fold = true

    init {
        _fold = true
        _positionList = mutableListOf()
    }

    @get:Bindable
    var positionList: MutableList<ShopPositionEntity>
        get() = _positionList ?: mutableListOf()
        set(value) {
            _positionList = value
            notifyPropertyChanged(BR.positionList)
        }

    // 折叠
    @get:Bindable
    var fold: Boolean
        get() = _fold
        set(value) {
            _fold = value
            notifyPropertyChanged(BR.fold)
        }
}