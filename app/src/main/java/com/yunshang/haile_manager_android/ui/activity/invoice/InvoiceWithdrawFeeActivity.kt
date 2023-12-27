package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.InvoiceUserEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.databinding.ItemInvoiceOperatorBinding
import com.yunshang.haile_manager_android.databinding.ItemInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import timber.log.Timber
import java.util.*

class InvoiceWithdrawFeeActivity :
    BaseBusinessActivity<ActivityInvoiceWithdrawFeeBinding, InvoiceWithdrawFeeViewModel>(
        InvoiceWithdrawFeeViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemInvoiceWithdrawFeeBinding, InvoiceWithdrawFeeEntity>(
            R.layout.item_invoice_withdraw_fee,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.cbInvoiceWithdrawFeeSelect?.setOnCheckedChangeListener { _, isChecked ->
                item.selected = isChecked
                refreshSelectBatchNum()
            }

            mItemBinding?.root?.setOnClickListener {
                item.selected = !item.selected
                refreshSelectBatchNum()
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_invoice_withdraw_fee

    override fun backBtn(): View = mBinding.barInvoiceWithdrawFeeTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        // 监听刷新
        LiveDataBus.with(BusEvents.INVOICE_FEE_LIST_STATUS)?.observe(this) {
            mBinding.rvInvoiceWithdrawFeeList.requestRefresh()
        }
    }

    private val dateDialog by lazy {
        DateSelectorDialog.Builder().apply {
            selectModel = 1
            minDate = Calendar.getInstance().apply { time = DateTimeUtils.beforeDay(Date(), 366) }
            maxDate = Calendar.getInstance().apply { time = Date() }
            onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                    Timber.i("----选择的开始日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")}")
                    Timber.i("----选择的结束日期${DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd")}")
                    //更换时间
                    mViewModel.startTime.value = date1
                    mViewModel.endTime.value = date2

                    mBinding.rvInvoiceWithdrawFeeList.requestRefresh()
                }
            }
        }.build()
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvInvoiceWithdrawFeeTime.setOnClickListener {
            dateDialog.show(
                supportFragmentManager,
                mViewModel.startTime.value,
                mViewModel.endTime.value
            )
        }
        mBinding.tvInvoiceWithdrawFeeOperator.setOnClickListener {
            mViewModel.invoiceUserList.value?.let { userList ->
                CommonNewBottomSheetDialog.Builder<InvoiceUserEntity, ItemInvoiceOperatorBinding>(
                    StringUtils.getString(R.string.invoice_operator),
                    userList,
                    multiSelect = true,
                    buildItemView = { _, data,_ ->
                        DataBindingUtil.inflate<ItemInvoiceOperatorBinding?>(
                            LayoutInflater.from(this@InvoiceWithdrawFeeActivity),
                            R.layout.item_invoice_operator,
                            null,
                            false
                        ).apply {
                            child = data
                        }
                    },
                    clickItemView = { _, data ->
                        if (-1 == data.id) {
                            userList.forEach {
                                it.commonItemSelect = data.commonItemSelect
                            }
                        } else {
                            userList.find { -1 == it.id }?.commonItemSelect = false
                        }
                    },
                ) {
                    mViewModel.selectInvoiceUserList.value =
                        mViewModel.invoiceUserList.value?.filter { item -> item.commonItemSelect }
                    mBinding.rvInvoiceWithdrawFeeList.requestRefresh()
                }.build().show(supportFragmentManager)
            }
        }

        mBinding.rvInvoiceWithdrawFeeList.layoutManager = LinearLayoutManager(this)
        mBinding.rvInvoiceWithdrawFeeList.adapter = mAdapter
        mBinding.rvInvoiceWithdrawFeeList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<InvoiceWithdrawFeeEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out InvoiceWithdrawFeeEntity>?) -> Unit
                ) {
                    mViewModel.requestInvoiceWithdrawFeeList(page, pageSize, callBack)
                }
            }

        mBinding.cbInvoiceWithdrawFeeAll.setOnCheckClickListener {
            if (!mBinding.cbInvoiceWithdrawFeeAll.isChecked) {
                selectAll()
            } else {
                resetSelectBatchNum()
            }
            true
        }

        mBinding.btnInvoiceWithdrawFeeOpen.setOnClickListener {
            startActivity(
                Intent(
                    this@InvoiceWithdrawFeeActivity,
                    IssueInvoiceActivity::class.java
                ).apply {
                    putExtras(IntentParams.IssueInvoiceParams.pack(mAdapter.list.filter { it.selected }))
                })
        }
    }

    private fun selectAll() {
        mAdapter.list.forEach {
            it.selected = 2 != it.invoiceStatus
        }
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    private fun resetSelectBatchNum() {
        mAdapter.list.forEach {
            it.selected = false
        }
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    private fun refreshSelectBatchNum() {
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    override fun initData() {
        mBinding.rvInvoiceWithdrawFeeList.requestRefresh()
    }
}