package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/20 14:51
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceCategoryEntity(
    val categoryCode: String? = "",
    val categoryId: Int? = null,
    val categoryName: String? = ""
) : IMultiSelectBottomItemEntity {
    override var isCheck: Boolean = false
    override var onlyOne: Boolean = false
    override fun getTitle(): String = categoryName ?: ""
}