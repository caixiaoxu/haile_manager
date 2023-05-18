package com.shangyun.haile_manager_android.data.arguments

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/18 11:23
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object DiscountsParam {

    /**
     * 优惠状态值
     */
    @JvmStatic
    fun getStatusVal(status: Int) = when (status) {
        1 -> "未开始"
        2 -> "已开始"
        3 -> "已过期"
        else -> "已信用"
    }
}
