package com.yunshang.haile_manager_android.ui.activity.device

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceUnbindApproveViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceUnbindApproveBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/16 13:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindApproveActivity :
    BaseBusinessActivity<ActivityDeviceUnbindApproveBinding, DeviceUnbindApproveViewModel>(
        DeviceUnbindApproveViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_device_unbind_approve

    override fun initView() {

    }

    override fun initData() {
    }
}