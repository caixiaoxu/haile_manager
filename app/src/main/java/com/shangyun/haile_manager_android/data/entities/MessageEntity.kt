package com.shangyun.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 10:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MessageEntity(
    val id: Int,
    val appType: Int,
    val typeId: Int,
    val subtypeId: Int,
    val subtype: String,
    val accountId: Int,
    val logo: String,
    val title: String,
    val content: String,
    val msgKey: String,
    val read: Int,
    val createTime: String,
    val readTime: String
)