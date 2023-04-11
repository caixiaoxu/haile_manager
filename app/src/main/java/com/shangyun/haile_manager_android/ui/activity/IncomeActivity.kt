package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.IncomeViewModel
import com.shangyun.haile_manager_android.databinding.ActivityIncomeBinding

class IncomeActivity : BaseBusinessActivity<ActivityIncomeBinding, IncomeViewModel>() {

    private val mIncomeViewModel by lazy {
        getActivityViewModelProvider(this)[IncomeViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_income


    override fun getVM(): IncomeViewModel = mIncomeViewModel
    override fun initView() {
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}