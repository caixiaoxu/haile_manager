package com.shangyun.haile_manager_android.ui.activity.login

import android.graphics.Color
import android.view.View
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.UpdatePasswordViewModel
import com.shangyun.haile_manager_android.databinding.ActivityUpdateLoginPasswordBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class UpdateLoginPasswordActivity :
    BaseBusinessActivity<ActivityUpdateLoginPasswordBinding, UpdatePasswordViewModel>(
        UpdatePasswordViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_update_login_password

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.canSubmit.observe(this) {
            mBinding.btnUpdateSure.isEnabled = it
        }
    }

    override fun initData() {
    }
}