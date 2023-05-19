package com.shangyun.haile_manager_android.ui.activity.subAccount

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SubAccountCreateViewModel
import com.shangyun.haile_manager_android.databinding.ActivitySubAccountCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class SubAccountCreateActivity :
    BaseBusinessActivity<ActivitySubAccountCreateBinding, SubAccountCreateViewModel>() {

    private val mSubAccountCreateViewModel by lazy {
        getActivityViewModelProvider(this)[SubAccountCreateViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_create

    override fun getVM(): SubAccountCreateViewModel = mSubAccountCreateViewModel

    override fun initView() {
    }

    override fun initData() {
    }
}