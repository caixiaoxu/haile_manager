package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.LoginForPasswordViewModel
import com.yunshang.haile_manager_android.data.ActivityTag
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityLoginForPasswordBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.MainActivity
import com.yunshang.haile_manager_android.utils.ViewUtils
import com.yunshang.haile_manager_android.web.WebViewActivity

class LoginForPasswordActivity :
    BaseBusinessActivity<ActivityLoginForPasswordBinding, LoginForPasswordViewModel>(
        LoginForPasswordViewModel::class.java,
        BR.vm
    ) {

    override fun activityTag(): String = ActivityTag.TAG_LOGIN
    override fun layoutId(): Int = R.layout.activity_login_for_password

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        mBinding.shared = mSharedViewModel

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement) {
            startActivity(Intent(this@LoginForPasswordActivity,WebViewActivity::class.java).apply {
                putExtras(
                    IntentParams.WebViewParams.pack(
                        BuildConfig.PRIVACY_POLICY,
                    )
                )
            })
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
        mViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {
        if (BuildConfig.DEBUG) {
            // 模拟数据
//            mViewModel.phone.value = "13067949521"
//            mViewModel.password.value = "Aa123456"
//            mViewModel.isAgree.value = true
        }
    }

    override fun jump(type: Int) {
        super.jump(type)
        startActivity(Intent(this, MainActivity::class.java))
        AppManager.finishAllActivityForTag(ActivityTag.TAG_LOGIN)
    }
}