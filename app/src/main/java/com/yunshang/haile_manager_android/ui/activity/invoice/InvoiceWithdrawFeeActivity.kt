package com.yunshang.haile_manager_android.ui.activity.invoice

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceWithdrawFeeActivity :
    BaseBusinessActivity<ActivityInvoiceWithdrawFeeBinding, InvoiceWithdrawFeeViewModel>(
        InvoiceWithdrawFeeViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_withdraw_fee

    override fun initView() {
    }

    override fun initData() {
    }
}