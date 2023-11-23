package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsReplyBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceRepairsReplyActivity :
    BaseBusinessActivity<ActivityDeviceRepairsReplyBinding, DeviceRepairsReplyViewModel>(
        DeviceRepairsReplyViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_repairs_reply

    override fun backBtn(): View = mBinding.barDeviceRepairsReplyTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}