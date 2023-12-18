package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.extend.formatMoney

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/21 11:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopDeviceRevenueListEntity(
    val id: Int,
    val name: String,
    val revenue: String
) {
    val revenueVal: String
        get() = revenue.formatMoney()
}