package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.StaffManagerViewModel
import com.shangyun.haile_manager_android.databinding.ActivityStaffManagerBinding

class StaffManagerActivity : BaseBusinessActivity<ActivityStaffManagerBinding, StaffManagerViewModel>() {
    private val mStaffManagerViewModel by lazy {
        getActivityViewModelProvider(this)[StaffManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_staff_manager

    override fun getVM(): StaffManagerViewModel =mStaffManagerViewModel

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}