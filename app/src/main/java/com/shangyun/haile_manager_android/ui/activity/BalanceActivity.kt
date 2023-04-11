package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.BalanceViewModel
import com.shangyun.haile_manager_android.databinding.ActivityBalanceBinding

class BalanceActivity : BaseBusinessActivity<ActivityBalanceBinding, BalanceViewModel>() {

    private val mBalanceViewModel by lazy {
        getActivityViewModelProvider(this)[BalanceViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_balance


    override fun getVM(): BalanceViewModel =mBalanceViewModel
    override fun initView() {
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}