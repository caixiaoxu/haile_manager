package com.yunshang.haile_manager_android.ui.activity.recharge

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeAccountsViewModel
import com.yunshang.haile_manager_android.business.vm.HaiXinRefundRecordViewModel
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeAccountsBinding
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRefundRecordBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinRefundRecordActivity :
    BaseBusinessActivity<ActivityHaixinRefundRecordBinding, HaiXinRefundRecordViewModel>(
        HaiXinRefundRecordViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_refund_record

    override fun initView() {
    }

    override fun initData() {
    }
}