package com.yunshang.haile_manager_android.data.rule

import androidx.lifecycle.MutableLiveData

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/23 14:27
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceIndicatorEntity<T>(
    val title: String,
    var num: MutableLiveData<Int> = MutableLiveData(0),
    val value: T,
)
