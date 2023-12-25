package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleDetailsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleDetailsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewDialog

class InvoiceTitleDetailsActivity :
    BaseBusinessActivity<ActivityInvoiceTitleDetailsBinding, InvoiceTitleDetailsViewModel>(
        InvoiceTitleDetailsViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_invoice_title_details

    override fun backBtn(): View = mBinding.barInvoiceTitleDetailsTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.invoiceTitleId = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
        LiveDataBus.with(BusEvents.INVOICE_TITLE_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestData()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnInvoiceTitleEdit.setOnClickListener {
            mViewModel.invoiceTitle.value?.let {
                startActivity(
                    Intent(
                        this@InvoiceTitleDetailsActivity,
                        InvoiceTitleAddActivity::class.java
                    ).apply {
                        putExtras(IntentParams.InvoiceTitleParams.pack(it))
                    })
            }
        }

        mBinding.btnInvoiceTitleDel.setOnClickListener {
            CommonNewDialog.Builder("是否删除").apply {
                title = StringUtils.getString(R.string.tip)
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.sure)) {
                    mViewModel.deleteInvoiceTitle()
                }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}