package com.yunshang.haile_manager_android.ui.activity.order

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.OrderExecutiveLoggingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.OrderExecutiveLoggingEntity
import com.yunshang.haile_manager_android.databinding.ActivityOrderExecutiveLoggingBinding
import com.yunshang.haile_manager_android.databinding.ItemOrderExecutiveLoggingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class OrderExecutiveLoggingActivity :
    BaseBusinessActivity<ActivityOrderExecutiveLoggingBinding, OrderExecutiveLoggingViewModel>(
        OrderExecutiveLoggingViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemOrderExecutiveLoggingBinding, OrderExecutiveLoggingEntity>(
            R.layout.item_order_executive_logging, BR.item
        ) { mItemBinding, pos, item ->

            mItemBinding?.viewOrderExecutiveLoggingLineUp?.visibility =
                if (0 == pos) View.GONE else View.VISIBLE
            if ((mViewModel.executiveLoggingList.value!!.size - 1) == pos) {
                mItemBinding?.viewOrderExecutiveLoggingLineDown?.visibility = View.GONE
                mItemBinding?.ivOrderExecutiveLoggingDot?.setImageResource(R.mipmap.icon_order_executive_logging_dot)
            } else {
                mItemBinding?.ivOrderExecutiveLoggingDot?.setImageResource(R.drawable.shape_sfoa258_circle)
                mItemBinding?.viewOrderExecutiveLoggingLineDown?.visibility = View.VISIBLE
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_order_executive_logging

    override fun backBtn(): View = mBinding.barExecutiveLoggingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.orderId = IntentParams.OrderParams.parseOrderId(intent)
        mViewModel.orderNo = IntentParams.OrderParams.parseOrderNo(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.executiveLoggingList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        mBinding.rvExecutiveLogging.layoutManager = LinearLayoutManager(this)
        mBinding.rvExecutiveLogging.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.requestData()
    }
}