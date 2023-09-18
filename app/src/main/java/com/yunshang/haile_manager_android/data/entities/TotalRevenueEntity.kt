package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 14:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class TotalRevenueEntity(
    val revenue: Double,
    val userFundList: List<UserFund>
)

data class UserFund(
    val account: String,
    val name: String,
    val revenue: Double,
    val userId: Int
)