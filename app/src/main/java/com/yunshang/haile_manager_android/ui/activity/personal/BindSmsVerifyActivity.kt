package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import com.lsy.framelib.utils.SoftKeyboardUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BindSmsVerifyViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityBindSmsVerifyBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class BindSmsVerifyActivity :
    BaseBusinessActivity<ActivityBindSmsVerifyBinding, BindSmsVerifyViewModel>(
        BindSmsVerifyViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_bind_sms_verify

    override fun backBtn(): View = mBinding.barBindAlipayTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.verifyType.value = IntentParams.BindSmsVerifyParams.parseAuthCode(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.authCode.observe(this) {
            if (0 == mViewModel.verifyType.value) {
                startActivity(
                    Intent(
                        this@BindSmsVerifyActivity,
                        WithdrawBindAlipayActivity::class.java
                    ).apply {
                        putExtras(intent)
                        putExtras(IntentParams.WithdrawBindAlipayParams.pack(it))
                    }
                )
            } else if (1 == mViewModel.verifyType.value) {
                startActivity(
                    Intent(
                        this@BindSmsVerifyActivity,
                        BankCardBindActivity::class.java
                    ).apply {
                        putExtras(intent)
                        putExtras(IntentParams.WithdrawBindAlipayParams.pack(it))
                    }
                )
            }
            finish()
        }
        mViewModel.verifyCode.observe(this) {
            if (!it.isNullOrEmpty() && 6 == it.length) {
                mViewModel.checkSms()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.etBindAlipaySms.isLongClickable = false
    }

    override fun initData() {
        mViewModel.sendOperateSms()
    }
}