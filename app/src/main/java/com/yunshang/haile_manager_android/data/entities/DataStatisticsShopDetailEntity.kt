package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/26 17:09
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DataStatisticsShopDetailEntity(
    val deviceStatisticsVO: DeviceStatisticsVO?,
    val orderStatisticsVO: OrderStatisticsVO?,
    val userStatisticsVO: UserStatisticsVO?,
    val orderStatisticsProfitVO: OrderStatisticsProfitVO?,
)

data class DeviceStatisticsVO(
    val deviceActiveCount: String,
    val deviceActiveCountCompare: Double,
    val deviceActiveRate: String,
    val deviceActiveRateCompare: Double,
    val deviceFaultRate: String,
    val deviceFaultRateCompare: Double,
    val deviceOfflineRate: String,
    val deviceOfflineRateCompare: Double,
    val deviceTotalCount: String,
    val deviceTotalCountCompare: Double,
    val deviceUseFrequency: String,
    val deviceUseFrequencyCompare: Double,
)

data class OrderStatisticsVO(
    val orderInvalidCount: String,
    val orderInvalidCountCompare: Double,
    val orderInvalidRate: String,
    val orderInvalidRateCompare: Double,
    val orderRefundAmount: String,
    val orderRefundAmountCompare: Double,
    val orderRefundCount: String,
    val orderRefundCountCompare: Double,
    val orderRevenueAmount: String,
    val orderRevenueAmountCompare: Double,
    val totalCount: String,
    val totalCountCompare: Double,
)
data class UserStatisticsVO(
    val userActiveCount: String,
    val userActiveCountCompare: Double,
    val userActivePercent: String,
    val userActivePercentCompare: Double,
    val userAddCount: String,
    val userAddCountCompare: Double,
    val userAddPercent: String,
    val userAddPercentCompare: Double,
    val userRepeatBuyCount: String,
    val userRepeatBuyPercent: String,
    val userRepeatBuyPercentCompare: Double,
    val userTotalCount: String,
    val userTotalCountCompare: Double,
)

data class OrderStatisticsProfitVO(
    val deviceOrderCount: String,
    val deviceOrderCountCompare: Double,
    val orderRefundAmount: String,
    val orderRefundAmountCompare: Double,
    val orderRefundCount: String,
    val orderRefundCountCompare: Double,
    val rechargeOrderCount: String,
    val rechargeOrderCountCompare: Double,
    val totalCount: String,
    val totalCountCompare: Double,
    val deviceRefundOrderCount: String,
    val deviceRefundOrderCountCompare: Double,
    val rechargeRefundOrderCount: String,
    val rechargeRefundOrderCountCompare: Double,
)

