package com.yunshang.haile_manager_android.ui.activity.recharge

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeListViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRechargeListActivity :
    BaseBusinessActivity<ActivityHaixinRechargeListBinding, HaiXinRechargeListViewModel>(
        HaiXinRechargeListViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_recharge_list

    override fun initView() {
    }

    override fun initData() {
    }
}