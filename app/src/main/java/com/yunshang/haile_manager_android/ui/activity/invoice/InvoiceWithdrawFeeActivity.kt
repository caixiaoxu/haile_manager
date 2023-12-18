package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.InvoiceWithdrawFeeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsBinding
import com.yunshang.haile_manager_android.databinding.ItemInvoiceWithdrawFeeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceRepairsReplyListActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

class InvoiceWithdrawFeeActivity :
    BaseBusinessActivity<ActivityInvoiceWithdrawFeeBinding, InvoiceWithdrawFeeViewModel>(
        InvoiceWithdrawFeeViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemInvoiceWithdrawFeeBinding, DeviceRepairsEntity>(
            R.layout.item_invoice_withdraw_fee,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.cbDeviceRepairsSelect?.setOnCheckedChangeListener { _, isChecked ->
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

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvInvoiceWithdrawFeeList.layoutManager = LinearLayoutManager(this)
        mBinding.rvInvoiceWithdrawFeeList.adapter = mAdapter
        mBinding.rvInvoiceWithdrawFeeList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceRepairsEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DeviceRepairsEntity>?) -> Unit
                ) {
                    if (UserPermissionUtils.hasRepairsListPermission()) {
                        mViewModel.requestDeviceRepairsList(page, pageSize, callBack)
                    }
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
    }

    private fun selectAll() {
        mAdapter.list.forEach {
            it.selected = true
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
    }
}