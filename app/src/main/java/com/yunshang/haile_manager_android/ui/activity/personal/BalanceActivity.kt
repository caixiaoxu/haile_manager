package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BalanceViewModel
import com.yunshang.haile_manager_android.data.entities.BalanceEntity
import com.yunshang.haile_manager_android.data.entities.BalanceListEntity
import com.yunshang.haile_manager_android.databinding.ActivityBalanceBinding
import com.yunshang.haile_manager_android.databinding.ItemBalanceBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.Calendar

class BalanceActivity : BaseBusinessActivity<ActivityBalanceBinding, BalanceViewModel>(
    BalanceViewModel::class.java,
) {

    override fun layoutId(): Int = R.layout.activity_balance
    override fun backBtn(): View = mBinding.barBalanceTitle.getBackBtn()

    private val mAdapter: CommonRecyclerAdapter<ItemBalanceBinding, BalanceListEntity> by lazy {
        object : CommonRecyclerAdapter<ItemBalanceBinding, BalanceListEntity>(
            R.layout.item_balance, BR.item,
            { mItemBinding, pos, item ->
                if (0 == pos || !DateTimeUtils.isSameMonth(
                        item.month,
                        mAdapter.list[pos - 1].month
                    )
                ) {
                    mItemBinding?.tvBalanceMonth?.text =
                        DateTimeUtils.formatDateTime(item.month, "yyyy年MM月")
                    mItemBinding?.tvBalanceMonth?.visibility = View.VISIBLE
                } else {
                    mItemBinding?.tvBalanceMonth?.visibility = View.GONE
                }

                val balance = item.balanceEntity
                if (null != balance) {
                    mItemBinding?.groupItemBalance?.visibility = View.VISIBLE
                    mItemBinding?.tvItemBalanceEmpty?.visibility = View.GONE
                    mItemBinding?.viewItemBalanceParent?.setOnClickListener {
                        startActivity(
                            Intent(
                                this@BalanceActivity,
                                IncomeDetailActivity::class.java
                            ).apply {
                                putExtra(IncomeDetailActivity.IncomeId, balance.id)
                            })
                    }
                } else {
                    mItemBinding?.groupItemBalance?.visibility = View.GONE
                    mItemBinding?.tvItemBalanceEmpty?.visibility = View.VISIBLE
                }
            },
        ) {
            override fun bindingData(item: BalanceListEntity): Any? = item.balanceEntity
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.rvBalanceList.layoutManager = LinearLayoutManager(this)
        mBinding.rvBalanceList.adapter = mAdapter
        mBinding.rvBalanceList.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<BalanceEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out BalanceEntity>?) -> Unit
            ) {
                if (isRefresh) {
                    mViewModel.searchDate = DateTimeUtils.curMonthFirst
                }
                mViewModel.requestBalanceList(page, pageSize, callBack)
            }

            override fun onCommonDeal(
                responseList: ResponseList<out BalanceEntity>,
                isRefresh: Boolean
            ): Boolean {
                val list = if (responseList.items.isNullOrEmpty()) {
                    mutableListOf(
                        BalanceListEntity(mViewModel.searchDate, null)
                    )
                } else {
                    responseList.items!!.map {
                        BalanceListEntity(DateTimeUtils.formatDateFromString(it.cashOutTime), it)
                    }.toMutableList()
                }

                if ((responseList.items?.size ?: 0) < responseList.pageSize) {
                    mBinding.rvBalanceList.page = 1
                    mViewModel.searchDate = Calendar.getInstance().apply {
                        time = mViewModel.searchDate
                        add(Calendar.MONTH, -1)
                    }.time
                }

                // 刷新数据
                mAdapter.refreshList(list, isRefresh)
                return true
            }
        }
    }

    override fun initData() {
        mBinding.rvBalanceList.requestRefresh()
    }
}