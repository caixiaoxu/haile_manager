package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep1Fragment
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep2Fragment
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep3Fragment

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/24 10:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceCreateV2ViewModel : BaseViewModel() {
    // 步骤
    val step: MutableLiveData<Int> = MutableLiveData(0)

    // 步骤对应的fragment
    val deviceCreateStepFragments = listOf(
        DeviceCreateStep1Fragment(),
        DeviceCreateStep2Fragment(),
        DeviceCreateStep3Fragment(),
    )

}