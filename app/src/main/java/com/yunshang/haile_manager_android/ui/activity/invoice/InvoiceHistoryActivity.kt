package com.yunshang.haile_manager_android.ui.activity.invoice

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceHistoryViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceTitleViewModel
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.data.entities.InvoiceUserEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.data.entities.IssueInvoiceDetailsEntity
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceHistoryBinding
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceTitleBinding
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.databinding.ItemInvoiceHistoryBinding
import com.yunshang.haile_manager_android.databinding.ItemInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import timber.log.Timber
import java.util.*

class InvoiceHistoryActivity :
    BaseBusinessActivity<ActivityInvoiceHistoryBinding, InvoiceHistoryViewModel>(
        InvoiceHistoryViewModel::class.java, BR.vm
    ) {
    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemInvoiceHistoryBinding, IssueInvoiceDetailsEntity>(
            R.layout.item_invoice_history,
            BR.item
        ) { mItemBinding, _, item ->

            mItemBinding?.root?.setOnClickListener {
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_invoice_history

    override fun backBtn(): View = mBinding.barInvoiceHistoryTitle.getBackBtn()

    private val dateDialog by lazy {
        DateSelectorDialog.Builder().apply {
            selectModel = 1
            maxDate = Calendar.getInstance().apply { time = Date() }
            onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                    Timber.i("----选择的开始日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")}")
                    Timber.i("----选择的结束日期${DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd")}")
                    //更换时间
                    mViewModel.startTime.value = date1
                    mViewModel.endTime.value = date2

                    mBinding.rvInvoiceHistoryList.requestRefresh()
                }
            }
        }.build()
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvInvoiceHistoryTime.setOnClickListener {
            dateDialog.show(
                supportFragmentManager,
                mViewModel.startTime.value,
                mViewModel.endTime.value
            )
        }
        mBinding.tvInvoiceHistoryTime.setOnClickListener {
            mViewModel.invoiceUserList.value?.let { userList ->
                MultiSelectBottomSheetDialog.Builder(
                    StringUtils.getString(R.string.invoice_operator),
                    userList
                ).apply {
                    isCanSelectEmpty = true
                    onValueSureListener = object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<InvoiceUserEntity> {
                        override fun onValue(
                            selectData: List<InvoiceUserEntity>,
                            allSelectData: List<InvoiceUserEntity>
                        ) {
                            mViewModel.selectInvoiceUserList.value = selectData
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }

        mBinding.rvInvoiceHistoryList.layoutManager = LinearLayoutManager(this)
        mBinding.rvInvoiceHistoryList.adapter = mAdapter
        mBinding.rvInvoiceHistoryList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<IssueInvoiceDetailsEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out IssueInvoiceDetailsEntity>?) -> Unit
                ) {
                    mViewModel.requestInvoiceWithdrawFeeList(page, pageSize, callBack)
                }
            }
    }

    override fun initData() {
    }
}