package com.shangyun.haile_manager_android.ui.activity.personal

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.WalletViewModel
import com.shangyun.haile_manager_android.databinding.ActivityWalletBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class WalletActivity : BaseBusinessActivity<ActivityWalletBinding, WalletViewModel>(WalletViewModel::class.java) {

    override fun layoutId(): Int = R.layout.activity_wallet

    override fun initView() {
    }

    override fun initData() {
    }
}