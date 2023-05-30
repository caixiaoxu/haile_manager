package com.shangyun.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.BalanceViewModel
import com.shangyun.haile_manager_android.data.entities.BalanceEntity
import com.shangyun.haile_manager_android.databinding.ActivityBalanceBinding
import com.shangyun.haile_manager_android.databinding.ItemBalanceBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import java.util.Calendar

class BalanceActivity : BaseBusinessActivity<ActivityBalanceBinding, BalanceViewModel>(
    BalanceViewModel::class.java,
) {

    override fun layoutId(): Int = R.layout.activity_balance
    override fun backBtn(): View = mBinding.barBalanceTitle.getBackBtn()

    private val mAdapter: CommonRecyclerAdapter<ItemBalanceBinding, BalanceEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_balance, BR.item
        ) { mItemBinding, pos, item ->
            if (0 == pos || !DateTimeUtils.isSameMonth(
                    DateTimeUtils.formatDateFromString(item.cashOutTime),
                    DateTimeUtils.formatDateFromString(mAdapter.list[pos - 1].cashOutTime)
                )
            ) {
                mItemBinding?.tvBalanceMonth?.text =
                    DateTimeUtils.formatDateTimeForStr(item.cashOutTime, "yyyy年MM月")
                mItemBinding?.tvBalanceMonth?.visibility = View.VISIBLE
            } else {
                mItemBinding?.tvBalanceMonth?.visibility = View.GONE
            }

            mItemBinding?.viewItemBalanceParent?.setOnClickListener {
                startActivity(Intent(this@BalanceActivity, IncomeDetailActivity::class.java).apply {
                    putExtra(IncomeDetailActivity.IncomeId, item.id)
                })
            }

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
                if (responseList.items.size < responseList.pageSize) {
                    mBinding.rvBalanceList.page = 1
                    mViewModel.searchDate = Calendar.getInstance().apply {
                        time = mViewModel.searchDate
                        add(Calendar.MONTH, -1)
                    }.time
                }
                // 刷新数据
                mAdapter.refreshList(responseList.items, isRefresh)
                return true
            }
        }
    }

    override fun initData() {
        mBinding.rvBalanceList.requestRefresh()
    }
}