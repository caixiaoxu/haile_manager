package com.yunshang.haile_manager_android.data.arguments

import com.yunshang.haile_manager_android.data.entities.Spu

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/25 15:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceModelParam(
    val name: String,
    val list: MutableList<Spu>,
)
