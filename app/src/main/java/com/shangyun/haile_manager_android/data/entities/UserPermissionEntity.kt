package com.shangyun.haile_manager_android.data.entities

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
    val childList: List<Child>,
    val icon: String,
    val kind: String,
    val menuId: Int,
    val name: String,
    val orderNum: String,
    val parentId: Int,
    val perms: String,
    val shareType: Int,
    val type: String,
    val url: String
)

data class Child(
    val childList: List<Any>,
    val icon: String,
    val kind: String,
    val menuId: Int,
    val name: String,
    val orderNum: String,
    val parentId: Int,
    val perms: String,
    val shareType: Int,
    val type: String,
    val url: String
)