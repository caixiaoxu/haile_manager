package com.yunshang.haile_manager_android.ui.activity.device

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsReplyListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceRepairsReplyListActivity : BaseBusinessActivity<ActivityDeviceRepairsReplyListBinding, DeviceRepairsReplyViewModel>(
    DeviceRepairsReplyViewModel::class.java,BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_device_repairs_reply_list

    override fun initView() {
    }
    override fun initData() {

    }
}