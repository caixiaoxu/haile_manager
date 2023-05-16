package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title : 用户权限
 * Author: Lsy
 * Date: 2023/4/6 11:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class UserPermissionEntity(
    val menuId: Int,
    val parentId: Int,
    val name: String,
    val url: String,
    val perms: String,
    val type: String,
    val orderNum: String,
    val kind: String,
    val icon: String,
    val shareType: Int,
    val childList: List<UserPermissionEntity>
) : IMultiSelectBottomItemEntity {

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override var isCheck: Boolean = false

    override fun getTitle(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is UserPermissionEntity && other.menuId == menuId) return true
        return super.equals(other)
    }
}