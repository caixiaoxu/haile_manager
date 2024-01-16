package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/21 18:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MessageSubTypeEntity(
    val code: String,
    val id: Int,
    val name: String,
    val typeId: Int,
    val lastMessageTime: String? = null,
    val iconUrl: String? = null
)