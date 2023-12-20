package com.yunshang.haile_manager_android.ui.activity.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IssueInvoiceViewModel
import com.yunshang.haile_manager_android.databinding.ActivityIssueInvoiceBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class IssueInvoiceActivity : BaseBusinessActivity<ActivityIssueInvoiceBinding,IssueInvoiceViewModel>(
    IssueInvoiceViewModel::class.java, BR.vm
) {

    override fun layoutId(): Int =R.layout.activity_issue_invoice

    override fun initView() {
    }

    override fun initData() {
    }
}