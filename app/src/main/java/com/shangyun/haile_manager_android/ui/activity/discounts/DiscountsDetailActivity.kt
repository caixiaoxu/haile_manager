package com.shangyun.haile_manager_android.ui.activity.discounts

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DiscountsDetailViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsDetailBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class DiscountsDetailActivity : BaseBusinessActivity<ActivityDiscountsDetailBinding, DiscountsDetailViewModel>() {

    private val mDiscountsDetailViewModel by lazy {
        getActivityViewModelProvider(this)[DiscountsDetailViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_discounts_detail

    override fun getVM(): DiscountsDetailViewModel =mDiscountsDetailViewModel

    override fun initView() {

    }

    override fun initData() {
    }
}