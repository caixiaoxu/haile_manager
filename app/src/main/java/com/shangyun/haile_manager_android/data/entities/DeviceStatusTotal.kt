package com.shangyun.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/23 14:45
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceStatusTotal(
    val `10`: Int,
    val `20`: Int,
    val `30`: Int,
    val `40`: Int
) {

    fun getTotal(type: Int) = when (type) {
        10 -> `10`
        20 -> `20`
        30 -> `30`
        40 -> `40`
        else ->0
    }

}