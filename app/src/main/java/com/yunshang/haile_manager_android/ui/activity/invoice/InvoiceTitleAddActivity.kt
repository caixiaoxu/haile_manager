package com.yunshang.haile_manager_android.ui.activity.invoice

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleAddViewModel
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleAddBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceTitleAddActivity :
    BaseBusinessActivity<ActivityInvoiceTitleAddBinding, InvoiceTitleAddViewModel>(
        InvoiceTitleAddViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_title_add

    override fun initView() {
    }

    override fun initData() {
    }
}