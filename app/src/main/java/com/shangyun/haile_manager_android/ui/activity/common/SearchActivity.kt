package com.shangyun.haile_manager_android.ui.activity.common

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.SoftKeyboardUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchType
import com.shangyun.haile_manager_android.data.rule.ISearchSelectEntity
import com.shangyun.haile_manager_android.databinding.ActivitySearchBinding
import com.shangyun.haile_manager_android.databinding.ItemSearchSelectBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.shangyun.haile_manager_android.ui.activity.shop.ShopDetailActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonLoadMoreRecyclerView
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class SearchActivity :
    BaseBusinessActivity<ActivitySearchBinding, SearchViewModel>() {
    private val mSearchViewModel by lazy {
        getActivityViewModelProvider(this)[SearchViewModel::class.java]
    }

    private val mAdapter: CommonRecyclerAdapter<ItemSearchSelectBinding, ISearchSelectEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_select,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.item = item
            mBinding?.root?.setOnClickListener {

                when (mSearchViewModel.searchType) {
                    SearchType.Device -> startActivity(
                        Intent(
                            this@SearchActivity,
                            DeviceDetailActivity::class.java
                        ).apply {
                            putExtra(DeviceDetailActivity.GoodsId, item.getSearchId())
                        }
                    )
                    SearchType.Shop -> startActivity(
                        Intent(
                            this@SearchActivity,
                            ShopDetailActivity::class.java
                        ).apply {
                            putExtra(ShopDetailActivity.ShopId, item.getSearchId())
                        }
                    )
                    SearchType.Order -> {}
                }
                finish()
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_search

    override fun getVM(): SearchViewModel = mSearchViewModel

    override fun initIntent() {
        intent.getIntExtra(SearchType.SearchType, -1).also {
            if (-1 != it)
                mSearchViewModel.searchType = it
        }
    }

    override fun initView() {
        mBinding.ibSearchBack.setOnClickListener {
            onBackListener()
        }
        mBinding.tvSearchSearch.setOnClickListener {
            SoftKeyboardUtils.hideShowKeyboard(mBinding.etSearchKey)
            if (SearchType.Shop == mSearchViewModel.searchType) {
                mBinding.rvSearchList1.requestLoadMore(true)
            } else {
                mBinding.rvSearchList2.requestRefresh(false)
            }
        }

        // 店铺搜索和（设备、订单）搜索响应数据格式不一
        if (SearchType.Shop == mSearchViewModel.searchType) {
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
                            mSearchViewModel.searchList(page, pageSize, result1 = callBack)
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
                        page: Int,
                        pageSize: Int,
                        callBack: (responseList: ResponseList<out ISearchSelectEntity>?) -> Unit
                    ) {
                        if ((SearchType.Device == mSearchViewModel.searchType
                                    && true == mSharedViewModel.hasDeviceListPermission.value)
                            || (SearchType.Order == mSearchViewModel.searchType
                                    && true == mSharedViewModel.hasOrderListPermission.value)
                        ) {
                            mSearchViewModel.searchList(page, pageSize, result2 = callBack)
                        }
                    }
                }
        }
    }

    override fun initData() {
        mBinding.vm = mSearchViewModel
    }
}