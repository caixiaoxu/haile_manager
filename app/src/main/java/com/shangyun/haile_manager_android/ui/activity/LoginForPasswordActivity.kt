package com.shangyun.haile_manager_android.ui.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.ui.base.BaseVMActivity
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.LoginForPasswordViewModel
import com.shangyun.haile_manager_android.databinding.ActivityLoginForPasswordBinding

class LoginForPasswordActivity : BaseVMActivity<LoginForPasswordViewModel>() {
    private val mBinding: ActivityLoginForPasswordBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_login_for_password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun rooView(): View = mBinding.root
    override fun initView() {
    }

    override fun initData() {
    }
}