package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ResetPasswordViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityResetPasswordBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.utils.ViewUtils
import com.yunshang.haile_manager_android.web.WebViewActivity

class ResetLoginPasswordActivity :
    BaseBusinessActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>(
        ResetPasswordViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_reset_password

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.phone.value = IntentParams.LoginParams.parsePhone(intent)
    }

    override fun initView() {

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement) {
            startActivity(Intent(this@ResetLoginPasswordActivity, WebViewActivity::class.java).apply {
                putExtras(
                    IntentParams.WebViewParams.pack(
                        BuildConfig.PRIVACY_POLICY,
                    )
                )
            })
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {
    }

    override fun jump(type: Int) {
        finish()
    }
}