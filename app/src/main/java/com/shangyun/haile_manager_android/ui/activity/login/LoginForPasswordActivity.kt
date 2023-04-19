package com.shangyun.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.BuildConfig
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.LoginForPasswordViewModel
import com.shangyun.haile_manager_android.data.ActivityTag
import com.shangyun.haile_manager_android.databinding.ActivityLoginForPasswordBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.MainActivity
import com.shangyun.haile_manager_android.utils.ViewUtils

class LoginForPasswordActivity :
    BaseBusinessActivity<ActivityLoginForPasswordBinding, LoginForPasswordViewModel>() {
    private val mLoginForPasswordViewModel by lazy {
        getActivityViewModelProvider(this)[LoginForPasswordViewModel::class.java]
    }

    override fun activityTag(): String = ActivityTag.TAG_LOGIN
    override fun layoutId(): Int = R.layout.activity_login_for_password

    override fun getVM(): LoginForPasswordViewModel = mLoginForPasswordViewModel

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        mBinding.vm = mLoginForPasswordViewModel
        mBinding.shared = mSharedViewModel

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement) {
            // TODO 跳转隐私协议
            SToast.showToast(this@LoginForPasswordActivity, "隐私协议")
        }

        // 忘记密码
        mBinding.tvLoginForgetPassword.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginForPasswordActivity,
                    ResetLoginPasswordActivity::class.java
                )
            )
        }
    }

    override fun initEvent() {
        super.initEvent()

        // 监听是否可点击提交按钮
        mLoginForPasswordViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {
        if (BuildConfig.DEBUG) {
            // 模拟数据
//            mLoginForPasswordViewModel.phone.value = "17028000053"
//            mLoginForPasswordViewModel.password.value = "Dp8Lv5"
            mLoginForPasswordViewModel.phone.value = "13067949521"
            mLoginForPasswordViewModel.password.value = "Aa123456"
            mLoginForPasswordViewModel.isAgree.value = true
        }
    }

    override fun jump(type: Int) {
        super.jump(type)
        startActivity(Intent(this, MainActivity::class.java))
        AppManager.finishAllActivityForTag(ActivityTag.TAG_LOGIN)
    }
}