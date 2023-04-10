package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceManagerViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDeviceManagerBinding

class DeviceManagerActivity : BaseBusinessActivity<ActivityDeviceManagerBinding,DeviceManagerViewModel>() {

    private val mDeviceManagerViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_device_manager


    override fun getVM(): DeviceManagerViewModel =mDeviceManagerViewModel
    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}