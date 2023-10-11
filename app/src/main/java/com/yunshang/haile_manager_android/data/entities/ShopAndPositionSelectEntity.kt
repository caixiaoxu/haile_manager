package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/24 10:08
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopAndPositionSelectEntity(
    val id: Int? = null,
    val name: String? = null,
    val positionList: List<ShopPositionSelect?>? = null
) : BaseObservable() {
    var fold = false
    val selectPositionList: MutableList<ShopPositionSelect> = mutableListOf()

    @get:Bindable
    var selectType: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectType)
        }
}

data class ShopPositionSelect(
    val code: String? = null,
    val deviceNum: Int? = null,
    val id: Int? = null,
    val name: String? = null,
    val sex: Int? = null,
    val shopId: Int? = null,
    val shopName: String? = null,
    val state: Int? = null,
    val workTime: String? = null,
    val workTimeStr: String? = null
)