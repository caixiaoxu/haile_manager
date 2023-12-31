package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.extend.formatMoney

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 14:10
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopRevenueEntity(
    val revenue: String,
    val shopId: Int,
    val shopName: String,
    val userFundList: List<UserFund>?
) {
    var fold = false

    val revenueVal: String
        get() = revenue.formatMoney()
}