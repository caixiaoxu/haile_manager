package com.yunshang.haile_manager_android.data.arguments

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
    fun getStatusVal(status: Int, expired: Int) =
        if (1 == status) "已停用" else if (1 == expired) "已过期" else "生效中"

    // 生效日模式
    val activeModel = arrayListOf(
        ActiveDayParam("每天", 1),
        ActiveDayParam("周一到周五", 3),
        ActiveDayParam("周六到周日", 4),
        ActiveDayParam("自定义生效日", 2),
    )

    val activeDay = arrayListOf(
        ActiveDayParam("周一", 1),
        ActiveDayParam("周二", 2),
        ActiveDayParam("周三", 3),
        ActiveDayParam("周四", 4),
        ActiveDayParam("周五", 5),
        ActiveDayParam("周六", 6),
        ActiveDayParam("周日", 0),
    )
}
