package com.yunshang.haile_manager_android.data.arguments

import android.view.View.OnClickListener
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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
    val show: MutableLiveData<Boolean>,
    val onClick:OnClickListener,
)
