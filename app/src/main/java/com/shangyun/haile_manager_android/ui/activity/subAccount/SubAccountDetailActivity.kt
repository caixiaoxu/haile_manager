package com.shangyun.haile_manager_android.ui.activity.subAccount

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SubAccountDetailViewModel
import com.shangyun.haile_manager_android.databinding.ActivitySubAccountDetailBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class SubAccountDetailActivity :
    BaseBusinessActivity<ActivitySubAccountDetailBinding, SubAccountDetailViewModel>() {

    private val mSubAccountDetailViewModel by lazy {
        getActivityViewModelProvider(this)[SubAccountDetailViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_detail

    override fun getVM(): SubAccountDetailViewModel = mSubAccountDetailViewModel

    override fun initView() {
    }

    override fun initData() {
    }
}