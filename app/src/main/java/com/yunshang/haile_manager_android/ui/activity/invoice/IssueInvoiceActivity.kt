package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IssueInvoiceViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityIssueInvoiceBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewDialog
import com.yunshang.haile_manager_android.utils.StringUtils

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
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.invoiceTitle.observe(this) {
            if (null == it) {
                showAddIssueTitleDialog()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

//        mBinding.includeIssueInvoiceTotal.root.visibility(true)
//        mBinding.includeIssueInvoiceTotal.tvInvoiceDetailsContent.text =
//            (mViewModel.selectFeeList?.fold(0.0) { sum, element -> sum + element.fee }
//                ?: 0.0).let { total ->
//                StringUtils.formatMultiStyleStr(
//                    "${total}元", arrayOf(
//                        ForegroundColorSpan(
//                            ResourcesCompat.getColor(
//                                Constants.APP_CONTEXT.resources,
//                                R.color.colorPrimary,
//                                null
//                            )
//                        ),
//                    ), 0, total.toString().length
//                )
//            }
    }


    private fun showAddIssueTitleDialog(){
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