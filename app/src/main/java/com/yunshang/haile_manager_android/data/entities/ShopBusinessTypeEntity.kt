package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity
import com.yunshang.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/19 16:22
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopBusinessTypeEntity(val type: Int, val businessName: String) :ICommonBottomItemEntity,
    IMultiSelectBottomItemEntity {
    override var isCheck: Boolean = false
    override fun getTitle(): String = businessName

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is ShopBusinessTypeEntity && other.type == type) return true

        return super.equals(other)
    }
}
