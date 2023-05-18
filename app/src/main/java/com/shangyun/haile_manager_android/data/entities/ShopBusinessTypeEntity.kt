package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

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
data class ShopBusinessTypeEntity(val type: Int, val businessName: String) :
    IMultiSelectBottomItemEntity {
    override var isCheck: Boolean = false
    override fun getTitle(): String = businessName
}
