package com.yunshang.haile_manager_android.ui.activity.device

import android.os.Bundle
import com.lsy.framelib.ui.base.activity.BaseActivity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceFunConfigurationAddV2Binding

class DeviceFunConfigurationAddV2Activity : BaseActivity() {
    private val mBinding: ActivityDeviceFunConfigurationAddV2Binding by lazy {
        ActivityDeviceFunConfigurationAddV2Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }
}