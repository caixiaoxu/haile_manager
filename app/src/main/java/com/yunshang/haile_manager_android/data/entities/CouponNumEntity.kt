package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/13 16:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CouponNumEntity(
    val couponStatusCountDTOS: List<CouponStatusCountDTOS>
)

data class CouponStatusCountDTOS(
    val count: Int,
    val status: Int?
)