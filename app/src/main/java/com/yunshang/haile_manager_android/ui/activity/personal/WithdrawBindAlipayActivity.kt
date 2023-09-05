package com.yunshang.haile_manager_android.ui.activity.personal

import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WithdrawBindAlipayViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityWithdrawBindAlipayBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class WithdrawBindAlipayActivity :
    BaseBusinessActivity<ActivityWithdrawBindAlipayBinding, WithdrawBindAlipayViewModel>(
        WithdrawBindAlipayViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_withdraw_bind_alipay

    override fun backBtn(): View = mBinding.barBindAlipayTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.id = IntentParams.CommonParams.parseId(intent)
        mViewModel.authCode = IntentParams.WithdrawBindAlipayParams.parseAuthCode(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
    }

    override fun initData() {}
}