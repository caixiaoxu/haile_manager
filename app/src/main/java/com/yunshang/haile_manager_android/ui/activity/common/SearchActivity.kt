package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.SoftKeyboardUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity
import com.yunshang.haile_manager_android.databinding.ActivitySearchBinding
import com.yunshang.haile_manager_android.databinding.ItemSearchHistoryFlowBinding
import com.yunshang.haile_manager_android.databinding.ItemSearchSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceManagerActivity
import com.yunshang.haile_manager_android.ui.activity.order.AppointmentOrderActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderManagerActivity
import com.yunshang.haile_manager_android.ui.activity.recharge.HaiXinRechargeAccountsActivity
import com.yunshang.haile_manager_android.ui.activity.recharge.HaiXinRefundRecordActivity
import com.yunshang.haile_manager_android.ui.activity.shop.ShopDetailActivity
import com.yunshang.haile_manager_android.ui.activity.subAccount.SubAccountManagerActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonLoadMoreRecyclerView
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

class SearchActivity :
    BaseBusinessActivity<ActivitySearchBinding, SearchViewModel>(
        SearchViewModel::class.java,
        BR.vm
    ) {

    private val mAdapter: CommonRecyclerAdapter<ItemSearchSelectBinding, ISearchSelectEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_select,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.item = item
            mBinding?.root?.setOnClickListener {

                when (mViewModel.searchType) {
                    SearchType.Device -> if (UserPermissionUtils.hasDeviceInfoPermission()) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                DeviceDetailActivity::class.java
                            ).apply {
                                putExtra(DeviceDetailActivity.GoodsId, item.getSearchId())
                            }
                        )
                    }
                    SearchType.Shop -> if (UserPermissionUtils.hasShopInfoPermission()) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                ShopDetailActivity::class.java
                            ).apply {
                                putExtra(ShopDetailActivity.ShopId, item.getSearchId())
                            }
                        )
                    }
                    SearchType.Order, SearchType.AppointOrder -> if (UserPermissionUtils.hasOrderInfoPermission()) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                OrderDetailActivity::class.java
                            ).apply {
                                putExtras(
                                    IntentParams.OrderDetailParams.pack(
                                        item.getSearchId(),
                                        mViewModel.searchType == SearchType.AppointOrder
                                    )
                                )
                            })
                    }
                }
                finish()
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_search

    override fun initIntent() {
        intent.getIntExtra(SearchType.SearchType, -1).also {
            if (-1 != it)
                mViewModel.searchType = it
        }
    }

    override fun initView() {
        mBinding.ibSearchBack.setOnClickListener {
            onBackListener()
        }
        mBinding.etSearchKey.onTextChange = {
            if (SearchType.Device == mViewModel.searchType) {
                search(true)
            }
        }
        mBinding.tvSearchSearch.setOnClickListener {
            search()
        }

        val childCount = mBinding.clSearchHistory.childCount
        if (childCount > 2) {
            mBinding.clSearchHistory.removeViews(2, childCount - 2)
        }
        val inflater = LayoutInflater.from(this)
        SPRepository.searchHistory?.let { history ->
            history.filter { item -> item.type == mViewModel.searchType }
                .forEachIndexed { index, searchParam ->
                    DataBindingUtil.inflate<ItemSearchHistoryFlowBinding>(
                        inflater,
                        R.layout.item_search_history_flow, null, false
                    ).also { itemBinding ->
                        itemBinding.name = searchParam.keyword
                        itemBinding.root.id = index + 1
                        itemBinding.root.setOnClickListener {
                            mViewModel.searchKey.value = searchParam.keyword.trim()
                            search()
                        }
                        mBinding.clSearchHistory.addView(itemBinding.root)
                    }
                }
            // 设置id
            val idList = IntArray(history.size) { it + 1 }
            mBinding.flowSearchHistory.referencedIds = idList
        }

        // 店铺搜索和（设备、订单）搜索响应数据格式不一
        if (SearchType.Shop == mViewModel.searchType) {
            mBinding.rvSearchList1.layoutManager = LinearLayoutManager(this)
            mBinding.rvSearchList1.adapter = mAdapter
            mBinding.rvSearchList1.requestData =
                object : CommonLoadMoreRecyclerView.OnRequestDataListener<ISearchSelectEntity>() {
                    override fun requestData(
                        page: Int,
                        pageSize: Int,
                        callBack: (responseList: MutableList<out ISearchSelectEntity>?) -> Unit
                    ) {
                        if (UserPermissionUtils.hasShopListPermission()) {
                            mViewModel.searchList(page, pageSize, result1 = callBack)
                        }
                    }
                }
        } else {
            mBinding.rvSearchList2.enableRefresh = false
            mBinding.rvSearchList2.layoutManager = LinearLayoutManager(this)
            if (SearchType.Device == mViewModel.searchType) {
                mBinding.rvSearchList2.listStatusImgResId = R.mipmap.icon_list_device_empty
                mBinding.rvSearchList2.listStatusTxtResId = R.string.empty_device
            }
            mBinding.rvSearchList2.adapter = mAdapter
            mBinding.rvSearchList2.requestData =
                object : CommonRefreshRecyclerView.OnRequestDataListener<ISearchSelectEntity>() {
                    override fun requestData(
                        isRefresh: Boolean,
                        page: Int,
                        pageSize: Int,
                        callBack: (responseList: ResponseList<out ISearchSelectEntity>?) -> Unit
                    ) {
                        if ((SearchType.Device == mViewModel.searchType
                                    && UserPermissionUtils.hasDeviceListPermission())
                            || (SearchType.Order == mViewModel.searchType
                                    && UserPermissionUtils.hasOrderListPermission())
                            || SearchType.AppointOrder == mViewModel.searchType
                        ) {
                            mViewModel.searchList(page, pageSize, result2 = callBack)
                        }
                    }
                }
        }
    }

    private fun search(autoSearch: Boolean = false) {
        // 保留历史
        val keyword = mViewModel.searchKey.value?.trim()
        if (!keyword.isNullOrEmpty() && !autoSearch) {
            SoftKeyboardUtils.hideShowKeyboard(mBinding.etSearchKey)
            val list = SPRepository.searchHistory ?: mutableListOf()
            list.find { item -> item.type == mViewModel.searchType && item.keyword == keyword }
                ?.let { params ->
                    list.remove(params)
                }
            list.add(SearchParam(mViewModel.searchType, keyword))
            SPRepository.searchHistory = list
        }

        when (mViewModel.searchType) {
            SearchType.Shop -> {
                if (keyword.isNullOrEmpty()) {
                    mBinding.clSearchHistory.visibility = View.VISIBLE
                    mBinding.rvSearchList1.visibility = View.GONE
                } else {
                    mBinding.rvSearchList1.requestLoadMore(true)
                    mBinding.clSearchHistory.visibility = View.GONE
                    // 显示搜索列表
                    mBinding.rvSearchList1.visibility = View.VISIBLE
                }
            }
            SearchType.Device -> {
                if (autoSearch) {
                    if (keyword.isNullOrEmpty()) {
                        mBinding.clSearchHistory.visibility = View.VISIBLE
                        mBinding.rvSearchList2.visibility = View.GONE
                    } else {
                        mBinding.rvSearchList2.requestRefresh(true)
                        mBinding.clSearchHistory.visibility = View.GONE
                        // 显示搜索列表
                        mBinding.rvSearchList2.visibility = View.VISIBLE
                    }
                } else {
                    startActivity(
                        Intent(
                            this@SearchActivity,
                            DeviceManagerActivity::class.java
                        ).apply {
                            putExtras(IntentParams.SearchParams.pack(mViewModel.searchKey.value))
                        })
                }
            }
            SearchType.Order -> startActivity(
                Intent(
                    this@SearchActivity,
                    OrderManagerActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchParams.pack(mViewModel.searchKey.value))
                })
            SearchType.AppointOrder -> startActivity(
                Intent(
                    this@SearchActivity,
                    AppointmentOrderActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchParams.pack(mViewModel.searchKey.value))
                })
            SearchType.HaiXinRefundRecord -> startActivity(
                Intent(
                    this@SearchActivity,
                    HaiXinRefundRecordActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchParams.pack(mViewModel.searchKey.value))
                })
            SearchType.HaiXinRechargeAccount -> startActivity(
                Intent(
                    this@SearchActivity,
                    HaiXinRechargeAccountsActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchParams.pack(mViewModel.searchKey.value))
                })
            SearchType.SubAccount -> startActivity(
                Intent(
                    this@SearchActivity,
                    SubAccountManagerActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchParams.pack(mViewModel.searchKey.value))
                })
        }
    }

    override fun initData() {
    }
}