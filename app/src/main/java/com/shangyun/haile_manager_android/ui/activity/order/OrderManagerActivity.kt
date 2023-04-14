package com.shangyun.haile_manager_android.ui.activity.order

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.OrderManagerViewModel
import com.shangyun.haile_manager_android.databinding.ActivityOrderManagerBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class OrderManagerActivity : BaseBusinessActivity<ActivityOrderManagerBinding, OrderManagerViewModel>() {

    private val mOrderManagerViewModel by lazy {
        getActivityViewModelProvider(this)[OrderManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_order_manager


    override fun getVM(): OrderManagerViewModel =mOrderManagerViewModel

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}