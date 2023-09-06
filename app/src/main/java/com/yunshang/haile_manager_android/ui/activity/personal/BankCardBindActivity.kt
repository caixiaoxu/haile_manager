package com.yunshang.haile_manager_android.ui.activity.personal

import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.databinding.ActivityBankCardBindBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class BankCardBindActivity :
    BaseBusinessActivity<ActivityBankCardBindBinding, BankCardBindViewModel>(
        BankCardBindViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_bank_card_bind

    override fun backBtn(): View = mBinding.barBankCardBindTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()


    }

    override fun initView() {
    }

    override fun initData() {
    }
}