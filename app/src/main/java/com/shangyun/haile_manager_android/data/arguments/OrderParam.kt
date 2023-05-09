package com.shangyun.haile_manager_android.data.arguments

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
    @JvmField
    val orderStatusList = hashMapOf(
        0 to "待支付",
        100 to "待支付",
        200 to "待支付",
        401 to "已取消",
        411 to "已取消",
        421 to "已取消",
        500 to "已支付",
        1000 to "已完成",
        2000 to "已退款",
        2050 to "已退款",
        2099 to "已退款",

        )
}