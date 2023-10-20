package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/20 14:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceStateCountsEntity(
    val enableCount: Int = 0,
    val enablePercent: Double = 0.0,
    val faultCount: Int = 0,
    val faultPercent: Double = 0.0,
    val freeCount: Int = 0,
    val freePercent: Double = 0.0,
    val offLineCount: Int = 0,
    val offLinePercent: Double = 0.0,
    val onLineCount: Int = 0,
    val onLinePercent: Double = 0.0,
    val totalCount: Int = 0,
    val unableCount: Int = 0,
    val unablePercent: Double = 0.0,
    val workCount: Int = 0,
    val workPercent: Double = 0.0
)