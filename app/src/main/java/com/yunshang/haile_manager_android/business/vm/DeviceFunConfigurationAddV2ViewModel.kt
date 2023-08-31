package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/31 19:18
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceFunConfigurationAddV2ViewModel : BaseViewModel() {

    val items: MutableLiveData<MutableList<ExtAttrDtoItem>> by lazy {
        MutableLiveData()
    }
}
