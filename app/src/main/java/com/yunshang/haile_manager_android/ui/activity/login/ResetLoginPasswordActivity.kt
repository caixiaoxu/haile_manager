package com.yunshang.haile_manager_android.ui.activity.login

import android.view.View
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ResetPasswordViewModel
import com.yunshang.haile_manager_android.databinding.ActivityResetPasswordBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.utils.ViewUtils

class ResetLoginPasswordActivity :
    BaseBusinessActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>(
        ResetPasswordViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_reset_password

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement) {
            // TODO 跳转隐私协议
            SToast.showToast(this@ResetLoginPasswordActivity, "隐私协议")
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {

        if (BuildConfig.DEBUG) {
            mViewModel.phone.value = "13067949521"
        }
    }

    override fun jump(type: Int) {
        finish()
    }
}