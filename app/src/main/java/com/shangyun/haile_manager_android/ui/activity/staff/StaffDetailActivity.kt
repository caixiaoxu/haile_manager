package com.shangyun.haile_manager_android.ui.activity.staff

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.StaffDetailViewModel
import com.shangyun.haile_manager_android.databinding.ActivityStaffDetailBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class StaffDetailActivity :
    BaseBusinessActivity<ActivityStaffDetailBinding, StaffDetailViewModel>() {

    private val mStaffDetailViewModel by lazy {
        getActivityViewModelProvider(this)[StaffDetailViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_staff_detail

    override fun getVM(): StaffDetailViewModel = mStaffDetailViewModel

    override fun initView() {
    }

    override fun initData() {
    }
}