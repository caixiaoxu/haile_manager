package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.RealNameViewModel
import com.shangyun.haile_manager_android.databinding.ActivityRealNameBinding

class RealNameActivity : BaseBusinessActivity<ActivityRealNameBinding, RealNameViewModel>() {

    private val mRealNameViewModel by lazy {
        getActivityViewModelProvider(this)[RealNameViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_real_name


    override fun getVM(): RealNameViewModel = mRealNameViewModel
    override fun initView() {
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}