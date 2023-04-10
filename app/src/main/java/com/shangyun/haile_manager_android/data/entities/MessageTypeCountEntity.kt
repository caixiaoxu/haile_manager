package com.shangyun.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 15:12
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MessageTypeCountEntity(
    val appType: Int,//	app类型
    val count: Int,//一级类型id
    val subtypeId: Int,//二级类型id
    val typeId: Int//消息数量
)