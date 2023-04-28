package com.shangyun.haile_manager_android.data.arguments

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/28 11:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ItemShowParam(
    val title: String,
    @DrawableRes val icon: Int,
    val show: LiveData<Boolean>,
)
