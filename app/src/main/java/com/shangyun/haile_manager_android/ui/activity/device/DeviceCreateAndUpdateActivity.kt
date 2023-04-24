package com.shangyun.haile_manager_android.ui.activity.device

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceCreateAndUpdateViewModel
import com.shangyun.haile_manager_android.databinding.ActivityBalanceBinding
import com.shangyun.haile_manager_android.databinding.ActivityDeviceCreateAndUpdateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceCreateAndUpdateActivity : BaseBusinessActivity<ActivityDeviceCreateAndUpdateBinding, DeviceCreateAndUpdateViewModel>() {

    private val mDeviceCreateAndUpdateViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceCreateAndUpdateViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_device_create_and_update

    override fun getVM(): DeviceCreateAndUpdateViewModel =mDeviceCreateAndUpdateViewModel

    override fun initView() {
    }

    override fun initData() {
    }
}