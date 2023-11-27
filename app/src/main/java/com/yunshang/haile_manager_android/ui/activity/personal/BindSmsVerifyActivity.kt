package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
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

        mViewModel.verifyType.value = IntentParams.BindSmsVerifyParams.parseVerifyType(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.authCode.observe(this) {
            if (IntentParams.BindSmsVerifyParams.parseNeedBack(intent)) {
                setResult(RESULT_OK, Intent().apply {
                    putExtras(intent)
                    putExtras(IntentParams.WithdrawBindAlipayParams.pack(it))
                })
            } else {
                when (mViewModel.verifyType.value) {
                    0 -> {
                        startActivity(
                            Intent(
                                this@BindSmsVerifyActivity,
                                WithdrawBindAlipayActivity::class.java
                            ).apply {
                                putExtras(intent)
                                putExtras(IntentParams.WithdrawBindAlipayParams.pack(it))
                            }
                        )
                    }
                    1 -> {
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
                    2 -> {
                        startActivity(
                            Intent(
                                this@BindSmsVerifyActivity,
                                RealNameAuthBindingActivity::class.java
                            ).apply {
                                putExtras(intent)
                                putExtras(IntentParams.WithdrawBindAlipayParams.pack(it))
                            }
                        )
                    }
                }
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
        mBinding.etBindAlipaySms.isFocusable = true
        mBinding.etBindAlipaySms.isFocusableInTouchMode = true
        mBinding.etBindAlipaySms.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                SoftKeyboardUtils.showKeyboard(mBinding.etBindAlipaySms)
            }
        }
    }

    override fun initData() {
        mViewModel.sendOperateSms() {
            if (!it) {
                finish()
            } else {
                mBinding.etBindAlipaySms.requestFocus()
            }
        }
    }
}