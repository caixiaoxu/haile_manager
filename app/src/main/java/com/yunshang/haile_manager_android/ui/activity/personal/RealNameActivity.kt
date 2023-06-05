package com.yunshang.haile_manager_android.ui.activity.personal

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RealNameViewModel
import com.yunshang.haile_manager_android.databinding.ActivityRealNameBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class RealNameActivity : BaseBusinessActivity<ActivityRealNameBinding, RealNameViewModel>(
    RealNameViewModel::class.java,
    BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_real_name
    override fun initView() {
    }

    override fun initData() {
    }
}