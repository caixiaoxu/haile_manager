package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsBatchReplyViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsBatchReplyBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceRepairsBatchReplyActivity :
    BaseBusinessActivity<ActivityDeviceRepairsBatchReplyBinding, DeviceRepairsBatchReplyViewModel>(
        DeviceRepairsBatchReplyViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_repairs_batch_reply

    override fun backBtn(): View = mBinding.barDeviceRepairsBatchReplyTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.deviceIds = IntentParams.DeviceFaultRepairsParams.parseDeviceIdList(intent)
        mViewModel.fixIds = IntentParams.DeviceFaultRepairsParams.parseFaultIdList(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}