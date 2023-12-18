package com.yunshang.haile_manager_android.ui.activity.invoice

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceHistoryViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceHistoryBinding
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleBinding
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceHistoryActivity :
    BaseBusinessActivity<ActivityInvoiceHistoryBinding, InvoiceHistoryViewModel>(
        InvoiceHistoryViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_history

    override fun initView() {
    }

    override fun initData() {
    }
}