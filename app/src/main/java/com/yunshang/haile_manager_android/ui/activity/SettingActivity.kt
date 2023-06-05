package com.yunshang.haile_manager_android.ui.activity

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.AppManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SettingViewModel
import com.yunshang.haile_manager_android.databinding.ActivitySettingBinding
import com.yunshang.haile_manager_android.ui.activity.login.ChangeUserActivity
import com.yunshang.haile_manager_android.ui.activity.login.LoginActivity
import com.yunshang.haile_manager_android.ui.activity.login.UpdateLoginPasswordActivity

class SettingActivity : BaseBusinessActivity<ActivitySettingBinding, SettingViewModel>(SettingViewModel::class.java,BR.vm) {

    override fun layoutId(): Int = R.layout.activity_setting

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvChangePassword.setOnClickListener {
            startActivity(Intent(this@SettingActivity, UpdateLoginPasswordActivity::class.java))
        }

        mBinding.tvChangeAccount.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeUserActivity::class.java))
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            AppManager.finishAllActivity()
            startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
        }
    }

    override fun initData() {
    }
}