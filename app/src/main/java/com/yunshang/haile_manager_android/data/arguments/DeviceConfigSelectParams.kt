package com.yunshang.haile_manager_android.data.arguments

import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/5 15:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceConfigSelectParams(
    val id: Int,
    val name: String,
    val times: List<Int>
) :
    ICommonBottomItemEntity {
    override fun getTitle(): String = name
}
