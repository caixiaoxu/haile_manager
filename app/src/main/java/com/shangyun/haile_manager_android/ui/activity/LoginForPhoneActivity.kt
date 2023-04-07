package com.shangyun.haile_manager_android.ui.activity

import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.BuildConfig
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.LoginForPhoneViewModel
import com.shangyun.haile_manager_android.data.ActivityTag
import com.shangyun.haile_manager_android.databinding.ActivityLoginForPhoneBinding
import com.shangyun.haile_manager_android.utils.ViewUtils

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
        ViewUtils.initAgreementToTextView(mBinding.tvLoginAgreement){
            // TODO 跳转隐私协议
            SToast.showToast(this@LoginForPhoneActivity, "隐私协议")
        }
    }

    override fun activityTag(): String = ActivityTag.TAG_LOGIN

    override fun initEvent() {
        super.initEvent()

        mLoginForPhoneViewModel.canSubmit.observe(this) {
            mBinding.btnLoginSure.isEnabled = it
        }
    }

    override fun initData() {
        if (BuildConfig.DEBUG){
            // 模拟数据
            mLoginForPhoneViewModel.phone.value = "13067949521"
            mLoginForPhoneViewModel.isAgree.value = true
        }
    }

    override fun jump(type: Int) {
        super.jump(type)
        startActivity(Intent(this,MainActivity::class.java))
        AppManager.finishAllActivityForTag(ActivityTag.TAG_LOGIN)
    }
}