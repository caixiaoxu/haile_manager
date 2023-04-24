package com.shangyun.haile_manager_android.data.entities

import androidx.lifecycle.MutableLiveData
import com.shangyun.haile_manager_android.data.rule.SearchSelectRadioEntity

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
data class ShopSelectEntity(
    val id: Int,
    val name: String
) : SearchSelectRadioEntity {
    @Transient
    var isSelect: MutableLiveData<Boolean>? = null
        get() {
            if (null == field) {
                field = MutableLiveData(false)
            }
            return field
        }

    override fun getSelectId(): Int = id

    override fun getSelectName(): String = name

    override fun getCheck(): MutableLiveData<Boolean>? = isSelect
}