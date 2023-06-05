package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/14 15:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopEntity(
    val id: Int,
    val name: String,
    val income: Double,
    val deviceNum: Int
)