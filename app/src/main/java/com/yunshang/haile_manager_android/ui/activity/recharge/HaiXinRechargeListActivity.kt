package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeListViewModel
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeEntity
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeListEntity
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeListBinding
import com.yunshang.haile_manager_android.databinding.ItemHaixinRechargeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

class HaiXinRechargeListActivity :
    BaseBusinessActivity<ActivityHaixinRechargeListBinding, HaiXinRechargeListViewModel>(
        HaiXinRechargeListViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_recharge_list

    override fun backBtn(): View = mBinding.barHaixinRechargeTitle.getBackBtn()

    private val mAdapter: CommonRecyclerAdapter<ItemHaixinRechargeBinding, HaixinRechargeListEntity> by lazy {
        object : CommonRecyclerAdapter<ItemHaixinRechargeBinding, HaixinRechargeListEntity>(
            R.layout.item_haixin_recharge, BR.item,
            { mItemBinding, pos, item ->
                if (0 == pos || !DateTimeUtils.isSameMonth(
                        item.month,
                        mAdapter.list[pos - 1].month
                    )
                ) {
                    mItemBinding?.tvHaixinRechargeMonth?.text =
                        DateTimeUtils.formatDateTime(item.month, "yyyy年MM月")
                    mItemBinding?.tvHaixinRechargeAmount?.text =
                        "${item.haiXinTotalEntity?.amount}${StringUtils.getString(R.string.unit_yuan)}"
                    mItemBinding?.tvHaixinRechargeHaixin?.text =
                        "${item.haiXinTotalEntity?.tokenCoinAmount}${StringUtils.getString(R.string.unit_ge)}"
                    mItemBinding?.groupItemMonth?.visibility = View.VISIBLE
                } else {
                    mItemBinding?.groupItemMonth?.visibility = View.GONE
                }

                val haixinRecharge = item.haixinRechargeEntity
                if (null != haixinRecharge) {
                    mItemBinding?.groupItemBalance?.visibility = View.VISIBLE
                    mItemBinding?.tvItemHaixinRechargeEmpty?.visibility = View.GONE
                    mItemBinding?.viewItemHaixinRechargeParent?.setOnClickListener {
                        startActivity(
                            Intent(
                                this@HaiXinRechargeListActivity,
                                IncomeDetailActivity::class.java
                            ).apply {
                                putExtra(IncomeDetailActivity.DetailType, 2)
                                putExtra(IncomeDetailActivity.IncomeId, haixinRecharge.id)
                            })
                    }
                } else {
                    mItemBinding?.groupItemBalance?.visibility = View.GONE
                    mItemBinding?.tvItemHaixinRechargeEmpty?.visibility = View.VISIBLE
                }
            },
        ) {
            override fun bindingData(item: HaixinRechargeListEntity): Any? =
                item.haixinRechargeEntity
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.rvHaixinRechargeList.layoutManager = LinearLayoutManager(this)
        mBinding.rvHaixinRechargeList.adapter = mAdapter
        mBinding.rvHaixinRechargeList.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<HaixinRechargeEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out HaixinRechargeEntity>?) -> Unit
            ) {
                if (isRefresh) {
                    mViewModel.searchDate = DateTimeUtils.curMonthFirst
                }
                mViewModel.requestHaixinRechargeList(page, pageSize, callBack)
            }

            override fun onCommonDeal(
                responseList: ResponseList<out HaixinRechargeEntity>,
                isRefresh: Boolean
            ): Boolean {
                val list = if (responseList.items.isEmpty()) {
                    mutableListOf(
                        HaixinRechargeListEntity(
                            mViewModel.searchDate,
                            mViewModel.totalHaixinOfMap[DateTimeUtils.formatDateTime(
                                mViewModel.searchDate,
                                "yyyy年MM月"
                            )],
                            null
                        )
                    )
                } else {
                    responseList.items.map {
                        val date = DateTimeUtils.formatDateFromString(it.createTime)
                        HaixinRechargeListEntity(
                            DateTimeUtils.formatDateFromString(it.createTime),
                            mViewModel.totalHaixinOfMap[DateTimeUtils.formatDateTime(
                                date,
                                "yyyy年MM月"
                            )],
                            it
                        )
                    }.toMutableList()
                }

                if (responseList.items.size < responseList.pageSize) {
                    mBinding.rvHaixinRechargeList.page = 1
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
        mBinding.rvHaixinRechargeList.requestRefresh()
    }
}