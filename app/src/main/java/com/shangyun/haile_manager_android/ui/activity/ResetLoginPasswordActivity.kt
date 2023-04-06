package com.shangyun.haile_manager_android.ui.activity

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.BuildConfig
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.ResetPasswordViewModel
import com.shangyun.haile_manager_android.databinding.ActivityResetPasswordBinding

class ResetLoginPasswordActivity : BaseBusinessActivity<ResetPasswordViewModel>() {
    private val mBinding: ActivityResetPasswordBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
    }

    private val mResetPasswordViewModel by lazy {
        getActivityViewModelProvider(this)[ResetPasswordViewModel::class.java]
    }

    override fun getVM(): ResetPasswordViewModel = mResetPasswordViewModel

    override fun rooView(): View = mBinding.root

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        mBinding.vm = mResetPasswordViewModel

        // 协议内容
        mBinding.tvLoginAgreement.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvLoginAgreement.highlightColor = Color.TRANSPARENT
        mBinding.tvLoginAgreement.text =
            SpannableString(resources.getString(R.string.login_agreement_hint)).apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@ResetLoginPasswordActivity,
                            R.color.colorPrimary
                        )
                    ),
                    6,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(view: View) {
                            // TODO 跳转隐私协议
                            SToast.showToast(this@ResetLoginPasswordActivity, "隐私协议")
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            //去掉下划线
                            ds.isUnderlineText = false
                        }
                    }, 6,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
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