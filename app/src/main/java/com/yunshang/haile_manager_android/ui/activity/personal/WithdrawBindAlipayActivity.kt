package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
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
        IntentParams.AlipayParams.parseAlipayAccount(intent)?.let {
            mViewModel.alipayAccount.value = it
        }
        IntentParams.AlipayParams.parseAlipayName(intent)?.let {
            mViewModel.alipayName.value = it
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.authCode.observe(this) {
            if (it.isNullOrEmpty()) {
                window.statusBarColor = Color.WHITE
                mBinding.root.setBackgroundColor(Color.WHITE)
            } else {
                window.statusBarColor = Color.TRANSPARENT
                mBinding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        this@WithdrawBindAlipayActivity,
                        R.color.page_bg
                    )
                )
            }
        }
        mViewModel.verifyCode.observe(this) {
            if (!it.isNullOrEmpty() && 6 == it.length) {
                mViewModel.checkSms()
            }
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
    }

    override fun initData() {}
}