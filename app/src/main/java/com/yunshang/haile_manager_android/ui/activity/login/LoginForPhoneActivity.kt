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
import com.yunshang.haile_manager_android.databinding.ActivityLoginForPhoneBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.MainActivity
import com.yunshang.haile_manager_android.utils.ViewUtils

class LoginForPhoneActivity : BaseBusinessActivity<ActivityLoginForPhoneBinding, LoginForPhoneViewModel>(
    LoginForPhoneViewModel::class.java,
    BR.vm) {

    override fun activityTag(): String = ActivityTag.TAG_LOGIN
    override fun layoutId(): Int =R.layout.activity_login_for_phone
    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        mBinding.shared = mSharedViewModel

        // 协议内容
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement){
            // TODO 跳转隐私协议
            SToast.showToast(this@LoginForPhoneActivity, "隐私协议")
        }
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {
        if (BuildConfig.DEBUG){
            // 模拟数据
            mViewModel.phone.value = "13067949521"
            mViewModel.isAgree.value = true
        }
    }

    override fun jump(type: Int) {
        super.jump(type)
        startActivity(Intent(this, MainActivity::class.java))
        AppManager.finishAllActivityForTag(ActivityTag.TAG_LOGIN)
    }
}