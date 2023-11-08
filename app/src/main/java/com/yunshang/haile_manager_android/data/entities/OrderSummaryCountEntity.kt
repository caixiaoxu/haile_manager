package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/8 13:46
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class OrderSummaryCountEntity(
    val closeCount: Int = 0,
    val faultCount: Int = 0,
    val reserveCount: Int = 0
)