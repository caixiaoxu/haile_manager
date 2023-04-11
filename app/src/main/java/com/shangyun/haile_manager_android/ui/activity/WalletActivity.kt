package com.shangyun.haile_manager_android.ui.activity

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.WalletViewModel
import com.shangyun.haile_manager_android.databinding.ActivityWalletBinding

class WalletActivity : BaseBusinessActivity<ActivityWalletBinding, WalletViewModel>() {

    private val mWalletViewModel by lazy {
        getActivityViewModelProvider(this)[WalletViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_wallet


    override fun getVM(): WalletViewModel =mWalletViewModel
    override fun initView() {
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}