package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/6 16:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceAdvancedEntity(
    val name: String,
    val type: String,
    val desc: String,
    val options: List<DeviceAdvancedOptionEntity>
) {
    var input: String? = ""
    var inputValue: String? = ""
}

data class DeviceAdvancedOptionEntity(val name: String, val value: String) :
    ICommonBottomItemEntity {
    override fun getTitle(): String = name

}
