package com.yunshang.haile_manager_android.ui.activity.recharge

import android.view.View
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRechargeActivity :
    BaseBusinessActivity<ActivityHaixinRechargeBinding, HaiXinRechargeViewModel>(
        HaiXinRechargeViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_recharge

    override fun backBtn(): View = mBinding.barHaixinRechargeTitle.getBackBtn()

    override fun initView() {
    }

    override fun initData() {
    }
}