package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/13 11:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class WithdrawCalculateEntity(
    val cashOutRate: Int,
    val feeAmount: String,
    val id: Int,
    val realAmount: String,
    val totalAmount: String
)