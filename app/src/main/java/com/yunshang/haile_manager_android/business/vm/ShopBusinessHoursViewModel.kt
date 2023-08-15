package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/14 10:56
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopBusinessHoursViewModel : BaseViewModel() {

    val businessHourList: MutableLiveData<MutableList<BusinessHourEntity>> by lazy {
        MutableLiveData()
    }

    fun initHourList(hoursStrings: MutableList<BusinessHourEntity>?) {
        businessHourList.postValue(
            if (hoursStrings.isNullOrEmpty()) {
                mutableListOf(BusinessHourEntity())
            } else {
                hoursStrings.apply {
                    add(BusinessHourEntity())
                }
            }
        )
    }

    fun addHourConfigure(v: View) {
        val list = businessHourList.value
        businessHourList.value = if (list.isNullOrEmpty()) {
            mutableListOf(BusinessHourEntity())
        } else {
            // 如果最后一个数据不为空，就添加
            val isLastEmpty = list.lastOrNull()?.let {
                it.weekDays.isEmpty() || it.workTime.isEmpty()
            } ?: false
            if (isLastEmpty) {
                list
            } else {
                list.apply { list.add(BusinessHourEntity()) }
            }
        }
    }
}