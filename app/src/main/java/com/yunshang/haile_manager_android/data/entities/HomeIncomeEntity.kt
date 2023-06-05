package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 18:53
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HomeIncomeEntity(
    val date: String,//时间
    val amount: Double,//金额
    val dateWeek: String//周几
)