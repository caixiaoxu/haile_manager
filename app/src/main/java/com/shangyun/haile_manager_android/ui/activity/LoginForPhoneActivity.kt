package com.shangyun.haile_manager_android.ui.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.ui.base.BaseVMActivity
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.LoginForPasswordViewModel
import com.shangyun.haile_manager_android.business.vm.LoginForPhoneViewModel
import com.shangyun.haile_manager_android.databinding.ActivityLoginForPhoneBinding

class LoginForPhoneActivity : BaseVMActivity<LoginForPhoneViewModel>() {

    private val mBinding: ActivityLoginForPhoneBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_login_for_phone)
    }

    private val mLoginForPhoneViewModel by lazy {
        getActivityViewModelProvider(this)[LoginForPhoneViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getVM(): LoginForPhoneViewModel =mLoginForPhoneViewModel

    override fun rooView(): View =mBinding.root

    override fun initView() {
    }

    override fun initData() {
    }
}