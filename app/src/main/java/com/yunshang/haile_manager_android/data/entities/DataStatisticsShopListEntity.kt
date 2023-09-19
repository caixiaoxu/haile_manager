package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/26 14:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DataStatisticsShopListEntity(
    val shopId: Int,
    val shopName: String,
    val deviceActiveCount: String,
    val deviceActiveCountCompare: Double,
    val deviceUseFrequency: String,
    val deviceUseFrequencyCompare: Double,
    val orderRevenueAmount: String,
    val orderRevenueAmountCompare: Double,
    val orderTotalCount: String,
    val orderTotalCountCompare: Double,
    val statisticsDate: String,
    val userActiveCount: String,
    val userActiveCountCompare: Double,
    val userAddCount: String,
    val userAddCountCompare: Double,

    val expend: String,
    val expendCompare: Double,
    val income: String,
    val incomeCompare: Double,
    val orderRevenueAmountInt: String,
    val revenue: String,
    val revenueCompare: Double,
    val deviceOrderCount: String,
    val deviceOrderCountCompare: Double
)