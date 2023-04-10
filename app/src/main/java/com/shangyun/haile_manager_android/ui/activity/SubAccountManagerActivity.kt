package com.shangyun.haile_manager_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceManagerViewModel
import com.shangyun.haile_manager_android.business.vm.SubAccountManagerViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDeviceManagerBinding
import com.shangyun.haile_manager_android.databinding.ActivitySubAccountManagerBinding

class SubAccountManagerActivity :
    BaseBusinessActivity<ActivitySubAccountManagerBinding, SubAccountManagerViewModel>() {

    private val mSubAccountManagerViewModel by lazy {
        getActivityViewModelProvider(this)[SubAccountManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_manager


    override fun getVM(): SubAccountManagerViewModel = mSubAccountManagerViewModel

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}