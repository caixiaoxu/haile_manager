package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.extend.formatMoney

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
    val revenue: String,
    val userFundList: List<UserFund>?,
    val deviceNum: Int,
) {
    var fold = false

    var deviceFold = false

    var page: Int = 1
    var noMore: Boolean = false
    var deviceList: MutableList<ShopDeviceRevenueListEntity>? = null

    val revenueVal: String
        get() = revenue.formatMoney()
}