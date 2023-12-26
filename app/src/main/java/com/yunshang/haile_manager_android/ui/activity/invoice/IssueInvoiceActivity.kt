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
import com.yunshang.haile_manager_android.data.entities.InvoiceReceiverEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.databinding.ActivityIssueInvoiceBinding
import com.yunshang.haile_manager_android.databinding.ItemIssueInvoiceReceiverSelectBinding
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

        mViewModel.jump.observe(this) {
            finish()
        }

        LiveDataBus.with(BusEvents.INVOICE_TITLE_LIST_STATUS)?.observe(this) {
            mViewModel.requestData()
        }

        LiveDataBus.with(BusEvents.INVOICE_TITLE_ADD_STATUS)?.observe(this) {
            mViewModel.requestData(true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rgIssueInvoiceType.setOnCheckedChangeListener { _, checkedId ->
            mViewModel.createInvoiceParams.value?.checkType(if (checkedId == R.id.rb_issue_invoice_type1) 2 else 1)
            if (checkedId == R.id.rb_issue_invoice_type1) {
                val paperInvoice =
                    mViewModel.invoiceReceiverList.value?.filter { it.email.isNullOrEmpty() }
                (paperInvoice?.find { it.commonItemSelect }
                    ?: paperInvoice?.firstOrNull())?.let { first ->
                    mViewModel.changeReceiver(first)
                }
            } else {
                mViewModel.createInvoiceParams.value?.clearReceiver()
            }
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
                    buildItemView = { _, data, _ ->
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

        // 手机号
        mBinding.ibIssueInvoicePhone.setOnClickListener {
            showPhoneEmailDialog()
        }

        // 邮箱
        mBinding.ibIssueInvoiceEmail.setOnClickListener {
            showPhoneEmailDialog()
        }

        // 地区
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

    private fun showPhoneEmailDialog() {
        val list = mViewModel.invoiceReceiverList.value?.filter { !it.email.isNullOrEmpty() }
            ?.toMutableList()
        CommonNewBottomSheetDialog.Builder<InvoiceReceiverEntity, ItemIssueInvoiceReceiverSelectBinding>(
            StringUtils.getString(R.string.single_select_dialog),
            list,
            buildItemView = { _, data, refreshSelectItem ->
                DataBindingUtil.inflate<ItemIssueInvoiceReceiverSelectBinding?>(
                    LayoutInflater.from(this@IssueInvoiceActivity),
                    R.layout.item_issue_invoice_receiver_select,
                    null,
                    false
                ).apply {
                    this.child = data
                    this.ibIssueInvoiceReceiverSelectDel.setOnClickListener {
                        mViewModel.deleteInvoiceTitle(data.id) {
                            list?.remove(data)
                            refreshSelectItem()
                        }
                    }
                }
            },
            emptyPromptTxt = "无手机号和电子邮箱记录"
        ) {
            list?.find { it.commonItemSelect }?.let {
                mViewModel.changeReceiver(it)
            }
        }.build().show(supportFragmentManager)
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
            isCancelable = false
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}