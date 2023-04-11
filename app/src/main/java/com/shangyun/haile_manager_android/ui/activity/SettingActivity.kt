package com.shangyun.haile_manager_android.ui.activity

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
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}