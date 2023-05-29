package com.shangyun.haile_manager_android.ui.activity.personal

import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.BankCardViewModel
import com.shangyun.haile_manager_android.databinding.ActivityBankCardBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class BankCardActivity : BaseBusinessActivity<ActivityBankCardBinding, BankCardViewModel>(BankCardViewModel::class.java,BR.vm) {

    override fun layoutId(): Int = R.layout.activity_bank_card

    override fun initView() {
    }

    override fun initData() {
    }
}