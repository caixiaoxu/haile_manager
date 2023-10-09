package com.yunshang.haile_manager_android.data.arguments

import com.yunshang.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/24 16:30
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MultiSelectParam(
    val id: Int,
    val name: String,
    override var isCheck: Boolean = false,
    override var onlyOne: Boolean = false,
) : IMultiSelectBottomItemEntity {
    override fun getTitle(): String = name
}