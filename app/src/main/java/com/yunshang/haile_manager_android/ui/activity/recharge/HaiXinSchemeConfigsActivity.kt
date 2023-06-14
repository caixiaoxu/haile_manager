package com.yunshang.haile_manager_android.ui.activity.recharge

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinSchemeConfigsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinSchemeConfigsActivity :
    BaseBusinessActivity<ActivityHaixinSchemeConfigsBinding, HaiXinSchemeConfigsViewModel>(
        HaiXinSchemeConfigsViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_scheme_configs

    override fun initView() {
    }

    override fun initData() {
    }
}