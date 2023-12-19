package com.yunshang.haile_manager_android.ui.activity.invoice

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleDetailsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleDetailsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceTitleDetailsActivity :
    BaseBusinessActivity<ActivityInvoiceTitleDetailsBinding, InvoiceTitleDetailsViewModel>(
        InvoiceTitleDetailsViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_title_details

    override fun initView() {
    }

    override fun initData() {
    }
}