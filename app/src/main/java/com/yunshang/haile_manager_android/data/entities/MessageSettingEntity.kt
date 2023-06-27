package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/27 12:06
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MessageSettingEntity(
    val accountId: Int,
    val id: Int,
    val status: Int,
    val subtypeId: Int,
    val subtypeName: String
)