package com.yunshang.haile_manager_android.ui.activity.personal

import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeStatisticsViewModel
import com.yunshang.haile_manager_android.data.entities.ShopRevenueEntity
import com.yunshang.haile_manager_android.data.entities.UserFund
import com.yunshang.haile_manager_android.databinding.ActivityIncomeStatisticsBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeStatisticsShopBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeStatisticsSubAccountInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.utils.StringUtils

class IncomeStatisticsActivity :
    BaseBusinessActivity<ActivityIncomeStatisticsBinding, IncomeStatisticsViewModel>(
        IncomeStatisticsViewModel::class.java, BR.vm
    ) {
    private val mAdapter: CommonRecyclerAdapter<ItemIncomeStatisticsShopBinding, ShopRevenueEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_statistics_shop,
            BR.item
        ) { mItemBinding, _, item ->

            if (item.userFundList.isNotEmpty()) {
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.root?.visibility(true)
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.llSubAccountInfo?.buildChild<ItemIncomeStatisticsSubAccountInfoBinding, UserFund>(
                    item.userFundList
                ) { _, childBinding, data ->
                    childBinding.child = data
                }
            } else mItemBinding?.includeItemIncomeStatisticsSubAccount?.root?.visibility(false)
        }
    }

    override fun isFullScreen(): Boolean = true

    override fun layoutId(): Int = R.layout.activity_income_statistics

    override fun backBtn(): View = mBinding.barIncomeStatisticsTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        // 总收益
        mViewModel.totalRevenue.observe(this) {
            it?.let { total ->
                mBinding.tvIncomeStatisticsRevenue.text = StringUtils.formatMultiStyleStr(
                    "¥${total.revenue}", arrayOf(
                        AbsoluteSizeSpan(DimensionUtils.sp2px(24f))
                    ), 0, 1
                )
                if (total.userFundList.isNotEmpty()) {
                    mBinding.includeIncomeStatisticsSubAccount.root.visibility(true)
                    mBinding.includeIncomeStatisticsSubAccount.llSubAccountInfo.buildChild<ItemIncomeStatisticsSubAccountInfoBinding, UserFund>(
                        total.userFundList
                    ) { _, childBinding, data ->
                        childBinding.child = data
                    }
                } else mBinding.includeIncomeStatisticsSubAccount.root.visibility(false)
            }
        }
    }

    override fun initView() {
        mBinding.barIncomeStatisticsTitle.getRightBtn().run {
            setText(R.string.income_expenses_detail)
            setTextColor(
                ContextCompat.getColor(
                    this@IncomeStatisticsActivity,
                    R.color.colorPrimary
                )
            )
            setOnClickListener {

            }
        }

        // 刷新加载
        mBinding.refreshLayout.setOnRefreshListener {
            requestData(true)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            requestData()
        }

        // 店铺列表
        mBinding.rvIncomeStatisticsList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(this, R.drawable.divide_size8)?.let {
            mBinding.rvIncomeStatisticsList.addItemDecoration(
                DividerItemDecoration(
                    this@IncomeStatisticsActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvIncomeStatisticsList.adapter = mAdapter
    }

    override fun initData() {
        requestData(true)
    }

    private fun requestData(isRefresh: Boolean = false) {
        if (isRefresh) {
            mBinding.refreshLayout.setEnableLoadMore(true)
        }

        mViewModel.requestData(isRefresh) { shopDataList ->
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            if (shopDataList.isEmpty()) {
                mBinding.refreshLayout.setEnableLoadMore(false)
            } else {
                mAdapter.refreshList(shopDataList, isRefresh)
            }
        }
    }
}