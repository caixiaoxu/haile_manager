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
    var positionList: MutableList<ShopPositionSelect>? = null
) : BaseObservable() {

    val shopAndPositionName: String
        get() = "${name ?: ""}-${positionList?.firstOrNull()?.name ?: ""}"

    @get:Bindable
    var fold = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.fold)
        }

    /**
     * 折叠或展开
     */
    fun changeFold() {
        fold = !fold
    }

    @get:Bindable
    var selectType: Int = 0
        //0全不选，1部分选中，2全选
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectType)
        }

    /**
     * 选择店铺或者不选
     */
    fun selectAllOrNo() {
        if (positionList.isNullOrEmpty()) {
            selectType = if (2 != selectType) 2 else 0
        } else {
            if (positionList!!.all { item -> !item.selectVal }) {
                positionList!!.forEach { item -> item.selectVal = true }
                selectType = 2
            } else {
                positionList!!.forEach { item -> item.selectVal = false }
                selectType = 0
            }
        }
    }

    /**
     * 检测是否全选或全不选
     */
    fun checkIsAll() {
        positionList?.let {
            selectType = if (it.all { item -> item.selectVal }) {
                2
            } else if (it.all { item -> !item.selectVal }) {
                0
            } else {
                1
            }
        }
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
) : BaseObservable() {

    @Transient
    @get:Bindable
    var selectVal: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectVal)
        }
}