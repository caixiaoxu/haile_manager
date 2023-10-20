package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/20 14:59
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceStateCountPercentEntity(
    val categoryCode: String = "",
    val categoryId: Int = 0,
    val categoryName: String = "",
    val count: Int = 0,
    val percent: Double = 0.0
)