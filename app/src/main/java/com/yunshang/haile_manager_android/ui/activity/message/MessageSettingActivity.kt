package com.yunshang.haile_manager_android.ui.activity.message

import android.view.View
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MessageSettingViewModel
import com.yunshang.haile_manager_android.databinding.ActivityMessageSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class MessageSettingActivity :
    BaseBusinessActivity<ActivityMessageSettingBinding, MessageSettingViewModel>(
        MessageSettingViewModel::class.java
    ) {
    override fun layoutId(): Int = R.layout.activity_message_setting

    override fun backBtn(): View = mBinding.barMessageSettingTitle.getBackBtn()

    override fun initView() {

    }

    override fun initData() {
    }
}