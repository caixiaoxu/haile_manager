package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.LoginForPhoneViewModel
import com.yunshang.haile_manager_android.data.ActivityTag
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityLoginForPhoneBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.MainActivity
import com.yunshang.haile_manager_android.utils.ViewUtils
import com.yunshang.haile_manager_android.web.WebViewActivity

class LoginForPhoneActivity : BaseBusinessActivity<ActivityLoginForPhoneBinding, LoginForPhoneViewModel>(
    LoginForPhoneViewModel::class.java,
    BR.vm) {

    override fun activityTag(): String = ActivityTag.TAG_LOGIN
    override fun layoutId(): Int =R.layout.activity_login_for_phone
    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.phone.value = IntentParams.LoginParams.parsePhone(intent)
    }

    override fun initView() {
        mBinding.shared = mSharedViewModel

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement){
            startActivity(Intent(this@LoginForPhoneActivity, WebViewActivity::class.java).apply {
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
        super.jump(type)
        startActivity(Intent(this, MainActivity::class.java))
        AppManager.finishAllActivityForTag(ActivityTag.TAG_LOGIN)
    }
}