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
    val enablePercent: Float = 0f,
    val faultCount: Int = 0,
    val faultPercent: Float = 0f,
    val freeCount: Int = 0,
    val freePercent: Float = 0f,
    val offLineCount: Int = 0,
    val offLinePercent: Float = 0f,
    val onLineCount: Int = 0,
    val onLinePercent: Float = 0f,
    val totalCount: Int = 0,
    val unableCount: Int = 0,
    val unablePercent: Float = 0f,
    val workCount: Int = 0,
    val workPercent: Float = 0f
)