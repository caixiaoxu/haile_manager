package com.yunshang.haile_manager_android.ui.activity.invoice

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceHistoryDetailsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceHistoryDetailsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceHistoryDetailsActivity :
    BaseBusinessActivity<ActivityInvoiceHistoryDetailsBinding, InvoiceHistoryDetailsViewModel>(
        InvoiceHistoryDetailsViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_history_details

    override fun initIntent() {
        super.initIntent()
        mViewModel.invoiceId = IntentParams.CommonParams.parseId(intent)
    }

    override fun initView() {
    }

    override fun initData() {
        mViewModel.requestData()
    }
}