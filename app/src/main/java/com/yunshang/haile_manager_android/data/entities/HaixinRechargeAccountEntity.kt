package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/16 17:33
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HaixinRechargeAccountEntity(
    val id: Int,
    val phone: String,
    val presentAmount: Int,
    val principalAmount: Int,
    val shopId: Int,
    val shopName: String,
    val totalAmount: Int,
    val userId: Int
)