package com.shangyun.haile_manager_android.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.LoginForPhoneViewModel
import com.shangyun.haile_manager_android.databinding.ActivityLoginForPhoneBinding

class LoginForPhoneActivity : BaseBusinessActivity<LoginForPhoneViewModel>() {

    private val mBinding: ActivityLoginForPhoneBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_login_for_phone)
    }

    private val mLoginForPhoneViewModel by lazy {
        getActivityViewModelProvider(this)[LoginForPhoneViewModel::class.java]
    }

    override fun getVM(): LoginForPhoneViewModel =mLoginForPhoneViewModel

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun rooView(): View =mBinding.root

    override fun initView() {
        mBinding.vm = mLoginForPhoneViewModel
        mBinding.shared = mSharedViewModel

        // 协议内容
        mBinding.tvLoginAgreement.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvLoginAgreement.highlightColor = Color.TRANSPARENT
        mBinding.tvLoginAgreement.text =
            SpannableString(resources.getString(R.string.login_agreement_hint)).apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@LoginForPhoneActivity,
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
                            SToast.showToast(this@LoginForPhoneActivity, "隐私协议")
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

        mLoginForPhoneViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initEvent() {
        super.initEvent()
        LiveDataBus.with(BusEvents.LOGIN_STATUS)?.observe(this){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    override fun initData() {
        // TODO 模拟数据
        mLoginForPhoneViewModel.phone.value = "13067949521"
        mLoginForPhoneViewModel.isAgree.value = true
    }
}