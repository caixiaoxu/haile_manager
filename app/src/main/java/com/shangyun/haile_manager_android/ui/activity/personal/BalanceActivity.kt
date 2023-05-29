package com.shangyun.haile_manager_android.ui.activity.personal

import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.BalanceViewModel
import com.shangyun.haile_manager_android.databinding.ActivityBalanceBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class BalanceActivity : BaseBusinessActivity<ActivityBalanceBinding, BalanceViewModel>(
    BalanceViewModel::class.java,
    BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_balance

    override fun initView() {
    }

    override fun initData() {
    }
}