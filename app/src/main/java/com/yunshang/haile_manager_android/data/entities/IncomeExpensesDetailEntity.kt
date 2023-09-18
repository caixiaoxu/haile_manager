package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 14:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class IncomeExpensesDetailEntity(
    val amount: Double,
    val businessTime: String,
    val categoryCode: String,
    val categoryName: String,
    val orderId: Int,
    val orderNo: String,
    val shopId: Int,
    val shopName: String,
    val transactionType: Int
)