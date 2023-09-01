package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/1 15:45
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceErrorStatusTotalEntity(
    val freeWaterCount: Int,
    val offlineCount: Int,
    val solenoidValveErrorCount: Int,
    val unusedCount: Int,
    val waterErrorCount: Int
)