package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/19 14:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class RefundRecordEntity(
    val account: String,
    val id: Int,
    val refundPrice: Double,
    val state: Int,
    val stateDesc: String,
    val userId: Int
)