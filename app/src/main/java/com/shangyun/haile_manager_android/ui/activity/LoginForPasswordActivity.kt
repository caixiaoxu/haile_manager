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
import com.shangyun.haile_manager_android.business.vm.SharedViewModel
import com.lsy.framelib.ui.base.BaseVMActivity
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.LoginForPasswordViewModel
import com.shangyun.haile_manager_android.databinding.ActivityLoginForPasswordBinding

class LoginForPasswordActivity : BaseBusinessActivity<LoginForPasswordViewModel>() {
    private val mBinding: ActivityLoginForPasswordBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_login_for_password)
    }
    private val mLoginForPasswordViewModel by lazy {
        getActivityViewModelProvider(this)[LoginForPasswordViewModel::class.java]
    }

    override fun rooView(): View = mBinding.root

    override fun getVM(): LoginForPasswordViewModel = mLoginForPasswordViewModel

    override fun initView() {
        mBinding.vm = mLoginForPasswordViewModel
        mBinding.shared = mSharedViewModel

        // 协议内容
        mBinding.tvLoginAgreement.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvLoginAgreement.highlightColor = Color.TRANSPARENT
        mBinding.tvLoginAgreement.text =
            SpannableString(resources.getString(R.string.login_agreement_hint)).apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@LoginForPasswordActivity,
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
                            SToast.showToast(this@LoginForPasswordActivity, "隐私协议")
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

        mLoginForPasswordViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {
        // TODO 模拟数据
        mLoginForPasswordViewModel.phone.value = "13067949521"
        mLoginForPasswordViewModel.password.value = "Ko7Ir9"
        mLoginForPasswordViewModel.isAgree.value = true
    }
}