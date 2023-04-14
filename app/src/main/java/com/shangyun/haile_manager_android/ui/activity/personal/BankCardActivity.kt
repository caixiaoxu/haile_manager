package com.shangyun.haile_manager_android.ui.activity.personal

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.BankCardViewModel
import com.shangyun.haile_manager_android.databinding.ActivityBankCardBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class BankCardActivity : BaseBusinessActivity<ActivityBankCardBinding, BankCardViewModel>() {

    private val mBankCardViewModel by lazy {
        getActivityViewModelProvider(this)[BankCardViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_bank_card


    override fun getVM(): BankCardViewModel = mBankCardViewModel
    override fun initView() {
    }

    override fun initData() {
        TODO("Not yet implemented")
    }
}