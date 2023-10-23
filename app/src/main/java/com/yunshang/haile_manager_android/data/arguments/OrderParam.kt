package com.yunshang.haile_manager_android.data.arguments

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/8 16:24
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object OrderParam {
    @JvmStatic
    fun orderStatusList(state: Int) = when (state) {
        50 -> "进行中"
        in 0 until 300 -> "待支付"
        400 -> "已取消"
        401 -> "支付超时"
        in 402 until 500 -> "已取消"
        in 500 until 600 -> "已支付"
        in 1000 until 1100 -> "已完成"
        in 2000 until 2100 -> "已退款"
        else -> ""
    }

    @JvmStatic
    fun getAppointStateName(code: Int): String = when (code) {
        0 -> "待支付"
        1 -> "待生效"
        2 -> "已生效"
        3 -> "已失效"
        4 -> "已取消"
        else -> ""
    }

    /**
     * 是否是退款
     */
    @JvmStatic
    fun isRefund(state: Int) =
        (2000 == state || 2050 == state || 2099 == state)
}