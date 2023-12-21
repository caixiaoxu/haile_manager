package com.yunshang.haile_manager_android.ui.activity.invoice

import android.view.View
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceExplainBinding

class InvoiceExplainActivity : BaseBindingActivity<ActivityInvoiceExplainBinding>() {

    override fun layoutId(): Int = R.layout.activity_invoice_explain

    override fun backBtn(): View = mBinding.barInvoiceExplainTitle.getBackBtn()
}