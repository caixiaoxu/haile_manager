package com.yunshang.haile_manager_android.ui.fragment.device

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceCreateStep1ViewModel
import com.yunshang.haile_manager_android.databinding.FragmentDeviceCreateStep1Binding
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
class DeviceCreateStep1Fragment :
    BaseBusinessFragment<FragmentDeviceCreateStep1Binding, DeviceCreateStep1ViewModel>(
        DeviceCreateStep1ViewModel::class.java
    ) {
    override fun layoutId(): Int = R.layout.fragment_device_create_step1

    override fun initView() {
    }

    override fun initData() {
    }
}