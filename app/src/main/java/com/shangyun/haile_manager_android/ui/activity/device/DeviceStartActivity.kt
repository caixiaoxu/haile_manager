package com.shangyun.haile_manager_android.ui.activity.device

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceStartViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDeviceStartBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceStartActivity : BaseBusinessActivity<ActivityDeviceStartBinding, DeviceStartViewModel>() {

    private val mDeviceStartViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceStartViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_device_start

    override fun getVM(): DeviceStartViewModel =mDeviceStartViewModel

    override fun initView() {

    }

    override fun initData() {
    }
}