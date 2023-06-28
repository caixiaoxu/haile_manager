package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.SoftKeyboardUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity
import com.yunshang.haile_manager_android.databinding.ActivitySearchBinding
import com.yunshang.haile_manager_android.databinding.ItemSearchSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.activity.shop.ShopDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonLoadMoreRecyclerView
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

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
                    SearchType.Device -> if (true == mSharedViewModel.hasDeviceInfoPermission.value) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                DeviceDetailActivity::class.java
                            ).apply {
                                putExtra(DeviceDetailActivity.GoodsId, item.getSearchId())
                            }
                        )
                    }
                    SearchType.Shop -> if (true == mSharedViewModel.hasShopInfoPermission.value) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                ShopDetailActivity::class.java
                            ).apply {
                                putExtra(ShopDetailActivity.ShopId, item.getSearchId())
                            }
                        )
                    }
                    SearchType.Order, SearchType.AppointOrder -> if (true == mSharedViewModel.hasOrderInfoPermission.value) {
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
        mBinding.tvSearchSearch.setOnClickListener {
            SoftKeyboardUtils.hideShowKeyboard(mBinding.etSearchKey)
            if (SearchType.Shop == mViewModel.searchType) {
                mBinding.rvSearchList1.requestLoadMore(true)
            } else {
                mBinding.rvSearchList2.requestRefresh(false)
            }
        }

        // 店铺搜索和（设备、订单）搜索响应数据格式不一
        if (SearchType.Shop == mViewModel.searchType) {
            mBinding.rvSearchList1.visibility = View.VISIBLE
            mBinding.rvSearchList1.layoutManager = LinearLayoutManager(this)
            mBinding.rvSearchList1.adapter = mAdapter
            mBinding.rvSearchList1.requestData =
                object : CommonLoadMoreRecyclerView.OnRequestDataListener<ISearchSelectEntity>() {
                    override fun requestData(
                        page: Int,
                        pageSize: Int,
                        callBack: (responseList: MutableList<out ISearchSelectEntity>?) -> Unit
                    ) {
                        if (true == mSharedViewModel.hasShopListPermission.value) {
                            mViewModel.searchList(page, pageSize, result1 = callBack)
                        }
                    }
                }
        } else {
            mBinding.rvSearchList2.visibility = View.VISIBLE
            mBinding.rvSearchList2.enableRefresh = false
            mBinding.rvSearchList2.layoutManager = LinearLayoutManager(this)
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
                                    && true == mSharedViewModel.hasDeviceListPermission.value)
                            || (SearchType.Order == mViewModel.searchType
                                    && true == mSharedViewModel.hasOrderListPermission.value)
                            || SearchType.AppointOrder == mViewModel.searchType
                        ) {
                            mViewModel.searchList(page, pageSize, result2 = callBack)
                        }
                    }
                }
        }
    }

    override fun initData() {
    }
}