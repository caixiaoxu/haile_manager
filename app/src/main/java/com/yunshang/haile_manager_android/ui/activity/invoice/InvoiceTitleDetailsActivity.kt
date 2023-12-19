package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleDetailsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleDetailsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class InvoiceTitleDetailsActivity :
    BaseBusinessActivity<ActivityInvoiceTitleDetailsBinding, InvoiceTitleDetailsViewModel>(
        InvoiceTitleDetailsViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_title_details

    override fun backBtn(): View = mBinding.barInvoiceTitleDetailsTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.invoiceTitle.value = IntentParams.InvoiceTitleParams.parseInvoiceTitle(intent)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.barInvoiceTitleDetailsTitle.getRightBtn(false).apply {
            setText(R.string.edit)
            setOnClickListener {
                mViewModel.invoiceTitle.value?.let {
                    startActivity(
                        Intent(
                            this@InvoiceTitleDetailsActivity,
                            InvoiceTitleAddActivity::class.java
                        ).apply {
                            IntentParams.InvoiceTitleParams.pack(it)
                        })
                }
            }
        }
    }

    override fun initData() {
    }
}