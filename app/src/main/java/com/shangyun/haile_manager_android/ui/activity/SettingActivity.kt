package com.shangyun.haile_manager_android.ui.activity

import android.content.Intent
import android.graphics.Color
import com.lsy.framelib.utils.AppManager
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SettingViewModel
import com.shangyun.haile_manager_android.databinding.ActivitySettingBinding

class SettingActivity : BaseBusinessActivity<ActivitySettingBinding, SettingViewModel>() {

    private val mSettingViewModel by lazy {
        getActivityViewModelProvider(this)[SettingViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_setting


    override fun getVM(): SettingViewModel = mSettingViewModel
    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.vm = mSettingViewModel

        mBinding.tvChangeAccount.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeAccountActivity::class.java))
        }
    }

    override fun initEvent() {
        super.initEvent()
        mSettingViewModel.jump.observe(this) {
            AppManager.finishAllActivity()
            startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
        }
    }

    override fun initData() {
    }
}