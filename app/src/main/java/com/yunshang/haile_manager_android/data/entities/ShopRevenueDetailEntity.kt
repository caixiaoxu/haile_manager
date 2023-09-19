package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 14:11
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopRevenueDetailEntity(
    val categoryCode: String,
    val categoryName: String,
    val revenue: Double,
    val userFundList: List<UserFund>?
)