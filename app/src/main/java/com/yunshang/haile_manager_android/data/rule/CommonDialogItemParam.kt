package com.yunshang.haile_manager_android.data.rule

import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/22 17:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CommonDialogItemParam(
    val id: Int,
    val name: String,
    val origin: String? = null,
) : ICommonNewBottomItemEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SearchSelectParam) return false
        else if (id == other.id) return true
        return super.equals(other)
    }
}
