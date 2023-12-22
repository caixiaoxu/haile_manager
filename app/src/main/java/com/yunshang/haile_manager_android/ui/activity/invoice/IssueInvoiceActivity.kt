package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.IssueInvoiceViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.databinding.ActivityIssueInvoiceBinding
import com.yunshang.haile_manager_android.databinding.ItemIssueInvoiceSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewDialog

class IssueInvoiceActivity :
    BaseBusinessActivity<ActivityIssueInvoiceBinding, IssueInvoiceViewModel>(
        IssueInvoiceViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_issue_invoice

    override fun backBtn(): View = mBinding.barIssueInvoiceTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.selectFeeList =
            IntentParams.IssueInvoiceParams.parseInvoiceWithdrawFeeEntity(intent)
        mViewModel.createInvoiceParams.value?.changeBusinessNos(mViewModel.selectFeeList?.mapNotNull { it.cashOutNo })
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.invoiceTitle.observe(this) {
            if (null == it) {
                showAddIssueTitleDialog()
            } else {
                mViewModel.createInvoiceParams.value?.changeInvoiceTemplateId(it.id)
            }
        }

        mViewModel.hasIssuePaperInvoice.observe(this) {
            mBinding.rgIssueInvoiceType.children.forEach { rb ->
                (rb as AppCompatRadioButton).setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, if (it) R.drawable.selector_invoice_type_checked else 0, 0
                )
            }
            mBinding.rgIssueInvoiceType.check(if (it) R.id.rb_issue_invoice_type1 else R.id.rb_issue_invoice_type2)
        }

        // 用于刷新接收人
        mViewModel.refreshReceiver.observe(this){}

        mViewModel.jump.observe(this){
            finish()
        }

        LiveDataBus.with(BusEvents.INVOICE_TITLE_LIST_STATUS)?.observe(this) {
            mViewModel.requestData()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rgIssueInvoiceType.setOnCheckedChangeListener { _, checkedId ->
            mViewModel.createInvoiceParams.value?.checkType(if (checkedId == R.id.rb_issue_invoice_type1) 2 else 1)
            mViewModel.createInvoiceParams.value?.clearReceiver()
        }

        mBinding.tvIssueInvoiceTitleMore.setOnClickListener {
            mViewModel.invoiceTitleList.value?.let {
                CommonNewBottomSheetDialog.Builder<InvoiceTitleEntity, ItemIssueInvoiceSelectBinding>(
                    StringUtils.getString(R.string.single_select_dialog),
                    it,
                    showBottomBtn = true,
                    bottomBtnTxt = StringUtils.getString(R.string.add_invoice_title),
                    bottomBtnEvent = {
                        startActivity(
                            Intent(
                                this@IssueInvoiceActivity,
                                InvoiceTitleAddActivity::class.java
                            )
                        )
                        2
                    },
                    buildItemView = { _, data ->
                        DataBindingUtil.inflate<ItemIssueInvoiceSelectBinding?>(
                            LayoutInflater.from(this@IssueInvoiceActivity),
                            R.layout.item_issue_invoice_select,
                            null,
                            false
                        ).apply {
                            this.child = data
                        }
                    }
                ) {
                    mViewModel.invoiceTitle.value =
                        mViewModel.invoiceTitleList.value?.find { item -> item.commonItemSelect }
                }.build().show(supportFragmentManager)
            }
        }

        mBinding.clIssueInvoiceArea.setOnClickListener {
            AreaSelectDialog.Builder().apply {
                onAreaSelect = { province, city, distract ->
                    mViewModel.createInvoiceParams.value?.changeArea(
                        province.areaId,
                        province.areaName,
                        city.areaId,
                        city.areaName,
                        distract.areaId,
                        distract.areaName
                    )
                }
            }.build().show(supportFragmentManager)
        }
    }

    private fun showAddIssueTitleDialog() {
        CommonNewDialog.Builder("无可用发票抬头，请先新增发票抬头").apply {
            title = com.lsy.framelib.utils.StringUtils.getString(R.string.tip)
            isNegativeShow = false
            setPositiveButton("去新增发票抬头") {
                startActivity(
                    Intent(
                        this@IssueInvoiceActivity,
                        InvoiceTitleAddActivity::class.java
                    )
                )
            }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}