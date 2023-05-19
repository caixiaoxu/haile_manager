package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.rule.ICommonBottomItemEntity
import com.shangyun.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/18 15:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DiscountsDeviceTypeEntity(
//    val code: String,
    val id: Int,
    val name: String
) : IMultiSelectBottomItemEntity {
    override var isCheck: Boolean = false

    override fun getTitle(): String = name

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is DiscountsDeviceTypeEntity && other.id == id) return true
        return super.equals(other)
    }
}