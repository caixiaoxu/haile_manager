package com.yunshang.haile_manager_android.ui.fragment.device

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceCreateStep1ViewModel
import com.yunshang.haile_manager_android.business.vm.DeviceCreateStep3ViewModel
import com.yunshang.haile_manager_android.databinding.FragmentDeviceCreateStep3Binding
import com.yunshang.haile_manager_android.ui.fragment.BaseBusinessFragment

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/24 11:14
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceCreateStep3Fragment :
    BaseBusinessFragment<FragmentDeviceCreateStep3Binding, DeviceCreateStep3ViewModel>(
        DeviceCreateStep3ViewModel::class.java
    ) {
    override fun layoutId(): Int = R.layout.fragment_device_create_step3

    override fun initView() {
    }

    override fun initData() {
    }
}