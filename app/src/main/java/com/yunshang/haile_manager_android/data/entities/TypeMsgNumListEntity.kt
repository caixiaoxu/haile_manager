package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/9 15:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */

data class TypeMsgNumListEntity(
    val moduleItems: List<TypeMsgNumEntity>? = null
)

data class TypeMsgNumEntity(
    val iconItems: List<IconItem>? = null,
    val moduleType: Int? = null
)

data class IconItem(
    val badgeNum: Int? = null,
    val icon: String? = null,
    val route: String? = null,
    val text: String? = null
)