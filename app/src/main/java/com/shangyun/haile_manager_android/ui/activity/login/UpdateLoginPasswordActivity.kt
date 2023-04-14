package com.shangyun.haile_manager_android.ui.activity.login

import android.graphics.Color
import android.view.View
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.UpdatePasswordViewModel
import com.shangyun.haile_manager_android.databinding.ActivityUpdateLoginPasswordBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class UpdateLoginPasswordActivity :
    BaseBusinessActivity<ActivityUpdateLoginPasswordBinding, UpdatePasswordViewModel>() {

    private val mUpdatePasswordViewModel by lazy {
        getActivityViewModelProvider(this)[UpdatePasswordViewModel::class.java]
    }

    override fun getVM(): UpdatePasswordViewModel = mUpdatePasswordViewModel

    override fun layoutId(): Int = R.layout.activity_update_login_password

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        mBinding.vm = mUpdatePasswordViewModel
        window.statusBarColor = Color.WHITE
    }

    override fun initEvent() {
        super.initEvent()
        mUpdatePasswordViewModel.canSubmit.observe(this) {
            mBinding.btnUpdateSure.isEnabled = it
        }
    }

    override fun initData() {
    }
}