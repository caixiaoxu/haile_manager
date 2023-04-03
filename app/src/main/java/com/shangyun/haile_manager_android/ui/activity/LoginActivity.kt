package com.shangyun.haile_manager_android.ui.activity

import android.os.Bundle
import android.view.View
import com.lsy.framelib.ui.base.BaseActivity
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.ActivityLoginBinding
import com.shangyun.haile_manager_android.utils.ActivityUtils

class LoginActivity : BaseActivity() {

    private val mLoginBinding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginBinding.actionTitleBar.setTitle(R.string.login_name)

        ActivityUtils.addDoubleBack(this,onBackPressedDispatcher)
    }

    override fun rooView(): View = mLoginBinding.root

    override fun backBtn(): View = mLoginBinding.actionTitleBar.getBackBtn()

    override fun onBackListener() {
        ActivityUtils.doubleBack(this)
    }
}