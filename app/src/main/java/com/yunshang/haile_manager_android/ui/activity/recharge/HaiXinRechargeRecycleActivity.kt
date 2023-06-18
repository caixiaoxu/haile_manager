package com.yunshang.haile_manager_android.ui.activity.recharge

import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeRecycleViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeRecycleBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRechargeRecycleActivity :
    BaseBusinessActivity<ActivityHaixinRechargeRecycleBinding, HaiXinRechargeRecycleViewModel>(
        HaiXinRechargeRecycleViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_recharge_recycle

    override fun backBtn(): View = mBinding.barRechargeRecycleTitle.getBackBtn()

    override fun initView() {
    }

    override fun initData() {
    }
}