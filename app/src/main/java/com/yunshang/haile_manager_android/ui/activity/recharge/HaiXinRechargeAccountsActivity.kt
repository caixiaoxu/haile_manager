package com.yunshang.haile_manager_android.ui.activity.recharge

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeAccountsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeAccountsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRechargeAccountsActivity :
    BaseBusinessActivity<ActivityHaixinRechargeAccountsBinding, HaiXinRechargeAccountsViewModel>(
        HaiXinRechargeAccountsViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_recharge_accounts

    override fun initView() {
    }

    override fun initData() {
    }
}