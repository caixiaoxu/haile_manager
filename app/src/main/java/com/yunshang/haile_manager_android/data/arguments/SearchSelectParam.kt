package com.yunshang.haile_manager_android.data.arguments

import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity

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
data class SearchSelectParam(
    val id: Int,
    val name: String,
    val origin: String? = null,
) : ICommonBottomItemEntity {
    override fun getTitle(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SearchSelectParam) return false
        else if (id == other.id) return true
        return super.equals(other)
    }
}