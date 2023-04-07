package com.shangyun.haile_manager_android.ui.activity

import android.view.View
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.BuildConfig
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ResetPasswordViewModel
import com.shangyun.haile_manager_android.databinding.ActivityResetPasswordBinding
import com.shangyun.haile_manager_android.utils.ViewUtils

class ResetLoginPasswordActivity :
    BaseBusinessActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>() {

    private val mResetPasswordViewModel by lazy {
        getActivityViewModelProvider(this)[ResetPasswordViewModel::class.java]
    }

    override fun getVM(): ResetPasswordViewModel = mResetPasswordViewModel

    override fun layoutId(): Int =R.layout.activity_reset_password

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        mBinding.vm = mResetPasswordViewModel

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement) {
            // TODO 跳转隐私协议
            SToast.showToast(this@ResetLoginPasswordActivity, "隐私协议")
        }
    }

    override fun initEvent() {
        super.initEvent()
        mResetPasswordViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {

        if (BuildConfig.DEBUG) {
            mResetPasswordViewModel.phone.value = "13067949521"
        }
    }

    override fun jump(type: Int) {
        finish()
    }
}