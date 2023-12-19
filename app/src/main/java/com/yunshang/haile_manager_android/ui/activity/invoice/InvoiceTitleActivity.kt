package com.yunshang.haile_manager_android.ui.activity.invoice

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleBinding
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceTitleActivity :
    BaseBusinessActivity<ActivityInvoiceTitleBinding, InvoiceTitleViewModel>(
        InvoiceTitleViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_title

    override fun backBtn(): View = mBinding.barInvoiceTitleTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE



    }

    override fun initData() {
    }
}