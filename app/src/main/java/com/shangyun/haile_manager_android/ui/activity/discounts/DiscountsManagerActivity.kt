package com.shangyun.haile_manager_android.ui.activity.discounts

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DiscountsManagerViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsManagerBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class DiscountsManagerActivity : BaseBusinessActivity<ActivityDiscountsManagerBinding, DiscountsManagerViewModel>() {

    private val mDiscountsManagerViewModel by lazy {
        getActivityViewModelProvider(this)[DiscountsManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_discounts_manager


    override fun getVM(): DiscountsManagerViewModel =mDiscountsManagerViewModel

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}