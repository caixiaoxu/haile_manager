package com.shangyun.haile_manager_android.data.rule

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/25 15:18
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MultiTypeAdapterItem<T>(
    val type: Int,
    val data: T,
)
