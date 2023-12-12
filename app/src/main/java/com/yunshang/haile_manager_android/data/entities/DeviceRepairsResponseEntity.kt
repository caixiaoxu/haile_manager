package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.network.response.ResponseList
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/22 15:03
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceRepairsResponseEntity(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val items: MutableList<DeviceRepairsEntity>?,
    val notRepliedCount: Int? = null
)