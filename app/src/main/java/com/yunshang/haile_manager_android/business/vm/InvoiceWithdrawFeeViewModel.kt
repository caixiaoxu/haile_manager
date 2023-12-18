package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/18 10:36
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class InvoiceWithdrawFeeViewModel: BaseViewModel() {


    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<Int> = MutableLiveData(0)

    val selectBatchNumVal: LiveData<String> = selectBatchNum.map {
        if (0 == it) "" else "${StringUtils.getString(R.string.selected)} $it"
    }


    fun refreshSelectBatchNum(list: MutableList<DeviceRepairsEntity>) {
        selectBatchNum.value = list.count { item -> item.selected }
        isAll.value =
            if (list.isNotEmpty()) list.all { item -> 10 != item.replyStatus || item.selected } else false
    }
}