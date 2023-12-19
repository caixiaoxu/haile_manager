package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleDetailsViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleBinding
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.databinding.ItemInvoiceTitleBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility

class InvoiceTitleActivity :
    BaseBusinessActivity<ActivityInvoiceTitleBinding, InvoiceTitleViewModel>(
        InvoiceTitleViewModel::class.java, BR.vm
    ) {

    private val adapter by lazy {
        CommonRecyclerAdapter<ItemInvoiceTitleBinding, InvoiceTitleEntity>(
            R.layout.item_invoice_title,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@InvoiceTitleActivity,
                        InvoiceTitleDetailsActivity::class.java
                    ).apply {
                        putExtras(IntentParams.InvoiceTitleParams.pack(item))
                    })
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_invoice_title

    override fun backBtn(): View = mBinding.barInvoiceTitleTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.invoiceTitleList.observe(this) {
            mBinding.customRefreshRecyclerView.rvRefreshList.visibility(!it.isNullOrEmpty())
            mBinding.customRefreshRecyclerView.tvListStatus.visibility(it.isNullOrEmpty())
            adapter.refreshList(it, true)
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.INVOICE_TITLE_LIST_STATUS)?.observe(this) {
            mViewModel.requestData()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.customRefreshRecyclerView.refreshLayout.setEnableLoadMore(false)
        mBinding.customRefreshRecyclerView.tvListStatus.setText(R.string.empty_content)
        val padding = DimensionUtils.dip2px(this, 12f)
        mBinding.customRefreshRecyclerView.rvRefreshList.setPadding(padding, padding, padding, 0)
        mBinding.customRefreshRecyclerView.rvRefreshList.layoutManager = LinearLayoutManager(this)
        mBinding.customRefreshRecyclerView.rvRefreshList.adapter = adapter

        mBinding.btnInvoiceTitleAdd.setOnClickListener {
            startActivity(Intent(this@InvoiceTitleActivity, InvoiceTitleAddActivity::class.java))
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}