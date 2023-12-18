package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.data.entities.UserPermissionEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/17 15:29
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object StaffParam {
    private val specialRole = "合伙人"

    @JvmStatic
    fun isSpecialRole(role: String?) = false // role == specialRole
}

data class StaffPermissionParams(
    val parent: UserPermissionEntity,
    var child: List<UserPermissionEntity>?,
) : BaseObservable() {

    val childNum: Int = child?.size ?: 0

    @get:Bindable
    val selectNum: Int
        get() = child?.count { it.isCheck } ?: 0

}