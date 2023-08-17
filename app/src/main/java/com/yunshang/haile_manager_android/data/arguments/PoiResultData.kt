package com.yunshang.haile_manager_android.data.arguments

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/30 16:52
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class PoiResultData(
    val title: String,
    val address: String,
    val latitude:Double,
    val longitude:Double,
    val distance: Double
)
