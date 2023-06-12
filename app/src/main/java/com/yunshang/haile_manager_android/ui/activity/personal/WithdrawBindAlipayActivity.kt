package com.yunshang.haile_manager_android.ui.activity.personal

import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WithdrawBindAlipayViewModel
import com.yunshang.haile_manager_android.databinding.ActivityWithdrawBindAlipayBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class WithdrawBindAlipayActivity :
    BaseBusinessActivity<ActivityWithdrawBindAlipayBinding, WithdrawBindAlipayViewModel>(
        WithdrawBindAlipayViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_withdraw_bind_alipay

    override fun backBtn(): View= mBinding.barBindAlipayTitle.getBackBtn()

    override fun initView() {}

    override fun initData() {}
}