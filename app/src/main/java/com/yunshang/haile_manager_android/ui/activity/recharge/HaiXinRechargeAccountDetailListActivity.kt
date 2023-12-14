package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeAccountDetailListViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeAccountDetailEntity
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeAccountDetailsEntity
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeListBinding
import com.yunshang.haile_manager_android.databinding.ItemHaixinRechargeAccountDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

class HaiXinRechargeAccountDetailListActivity :
    BaseBusinessActivity<ActivityHaixinRechargeListBinding, HaiXinRechargeAccountDetailListViewModel>(
        HaiXinRechargeAccountDetailListViewModel::class.java
    ) {

    override fun layoutId(): Int = R.layout.activity_haixin_recharge_list

    override fun backBtn(): View = mBinding.barHaixinRechargeTitle.getBackBtn()

    private val mAdapter: CommonRecyclerAdapter<ItemHaixinRechargeAccountDetailBinding, HaixinRechargeAccountDetailsEntity> by lazy {
        object :
            CommonRecyclerAdapter<ItemHaixinRechargeAccountDetailBinding, HaixinRechargeAccountDetailsEntity>(
                R.layout.item_haixin_recharge_account_detail, BR.item,
                { mItemBinding, pos, item ->
                    if (0 == pos || !DateTimeUtils.isSameMonth(
                            item.month,
                            mAdapter.list[pos - 1].month
                        )
                    ) {
                        mItemBinding?.tvHaixinRechargeMonth?.text =
                            "${
                                DateTimeUtils.formatDateTime(
                                    item.month,
                                    "yyyy年MM月"
                                )
                            } ${mViewModel.shopName}"
                        mItemBinding?.tvHaixinRechargeAmountTitle?.text =
                            "${getString(R.string.reach_starfish)}："
                        mItemBinding?.tvHaixinRechargeAmount?.text =
                            "${item.haiXinRechargeAccountDetailTotal?.principalAmount}${
                                StringUtils.getString(
                                    R.string.unit_ge
                                )
                            }"
                        mItemBinding?.tvHaixinRechargeHaixinTitle?.text =
                            "${getString(R.string.reward_starfish)}："
                        mItemBinding?.tvHaixinRechargeHaixin?.text =
                            "${item.haiXinRechargeAccountDetailTotal?.presentAmount}${
                                StringUtils.getString(
                                    R.string.unit_ge
                                )
                            }"
                        mItemBinding?.groupItemMonth?.visibility = View.VISIBLE
                    } else {
                        mItemBinding?.groupItemMonth?.visibility = View.GONE
                    }

                    val haixinRecharge = item.haixinRechargeAccountDetailEntity
                    if (null != haixinRecharge) {
                        mItemBinding?.groupItemBalance?.visibility = View.VISIBLE
                        mItemBinding?.tvItemHaixinRechargeEmpty?.visibility = View.GONE
                        mItemBinding?.viewItemHaixinRechargeParent?.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@HaiXinRechargeAccountDetailListActivity,
                                    IncomeDetailActivity::class.java
                                ).apply {
                                    putExtra(IncomeDetailActivity.DetailType, 1)
                                    putExtra(IncomeDetailActivity.IncomeId, haixinRecharge.id)
                                })
                        }
                    } else {
                        mItemBinding?.groupItemBalance?.visibility = View.GONE
                        mItemBinding?.tvItemHaixinRechargeEmpty?.visibility = View.VISIBLE
                    }
                },
            ) {
            override fun bindingData(item: HaixinRechargeAccountDetailsEntity): Any? =
                item.haixinRechargeAccountDetailEntity
        }
    }

    override fun initIntent() {
        super.initIntent()
        mViewModel.userId = IntentParams.RechargeAccountDetailParams.parseUserId(intent)
        mViewModel.shopId = IntentParams.RechargeAccountDetailParams.parseShopId(intent)
        mViewModel.shopName = IntentParams.RechargeAccountDetailParams.parseShopName(intent) ?: ""
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barHaixinRechargeTitle.setTitle(R.string.user_detail)
        mBinding.rvHaixinRechargeList.layoutManager = LinearLayoutManager(this)
        mBinding.rvHaixinRechargeList.adapter = mAdapter
        mBinding.rvHaixinRechargeList.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<HaixinRechargeAccountDetailEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out HaixinRechargeAccountDetailEntity>?) -> Unit
            ) {
                if (isRefresh) {
                    mViewModel.searchDate = DateTimeUtils.curMonthFirst
                }
                mViewModel.requestHaixinRechargeList(page, pageSize, callBack)
            }

            override fun onCommonDeal(
                responseList: ResponseList<out HaixinRechargeAccountDetailEntity>,
                isRefresh: Boolean
            ): Boolean {
                val list = if (responseList.items.isNullOrEmpty()) {
                    mutableListOf(
                        HaixinRechargeAccountDetailsEntity(
                            mViewModel.searchDate,
                            mViewModel.totalHaixinOfMap[DateTimeUtils.formatDateTime(
                                mViewModel.searchDate,
                                "yyyy年MM月"
                            )],
                            null
                        )
                    )
                } else {
                    responseList.items!!.map {
                        val date = DateTimeUtils.formatDateFromString(it.createTime)
                        HaixinRechargeAccountDetailsEntity(
                            DateTimeUtils.formatDateFromString(it.createTime),
                            mViewModel.totalHaixinOfMap[DateTimeUtils.formatDateTime(
                                date,
                                "yyyy年MM月"
                            )],
                            it
                        )
                    }.toMutableList()
                }

                if ((responseList.items?.size ?: 0) < responseList.pageSize) {
                    mBinding.rvHaixinRechargeList.page = 1
                    mViewModel.searchDate = Calendar.getInstance().apply {
                        time = mViewModel.searchDate
                        add(Calendar.MONTH, -1)
                    }.time
                }

                mBinding.rvBalanceList.showRecyclerView(false)

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