package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SoftKeyboardUtils
import com.lsy.framelib.utils.SystemPermissionHelper
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.SearchViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSearchEntity
import com.yunshang.haile_manager_android.data.entities.ShopPositionSearch
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity
import com.yunshang.haile_manager_android.databinding.*
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceManagerActivity
import com.yunshang.haile_manager_android.ui.activity.order.AppointmentOrderActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderManagerActivity
import com.yunshang.haile_manager_android.ui.activity.recharge.HaiXinRechargeAccountsActivity
import com.yunshang.haile_manager_android.ui.activity.recharge.HaiXinRefundRecordActivity
import com.yunshang.haile_manager_android.ui.activity.shop.ShopDetailActivity
import com.yunshang.haile_manager_android.ui.activity.shop.ShopPositionDetailActivity
import com.yunshang.haile_manager_android.ui.activity.subAccount.SubAccountManagerActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.ui.view.refresh.CustomDividerItemDecoration
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import timber.log.Timber

class SearchActivity : BaseBusinessActivity<ActivitySearchBinding, SearchViewModel>(
    SearchViewModel::class.java, BR.vm
) {

    // 权限
    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
            if (result.values.any { it }) {
                // 授权权限成功
                startQRActivity(false)
            } else {
                // 授权失败
                SToast.showToast(this, R.string.empty_permission)
            }
        }

    private fun startQRActivity(isOne: Boolean) {
        startQRCodeScan.launch(
            Intent(
                this,
                WeChatQRCodeScanActivity::class.java
            ).apply {
                putExtra("isOne", isOne)
            })
    }

    // 二维码
    private val startQRCodeScan =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 扫码结果
            if (result.resultCode == RESULT_OK) {
                CameraScan.parseScanResult(result.data)?.let {
                    Timber.i("扫码:$it")
                    mViewModel.searchKey.value = it
                } ?: SToast.showToast(this, R.string.imei_code_error)
            }
        }

    private val mAdapter: CommonRecyclerAdapter<ItemSearchSelectBinding, ISearchSelectEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_select,
            BR.item
        ) { mBinding, _, item ->
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
                                putExtras(IntentParams.ShopParams.pack(item.getSearchId()))
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

    private val mShopAdapter: CommonRecyclerAdapter<ItemShopSearchSelectBinding, ShopAndPositionSearchEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_shop_search_select,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.llShopSearchInfo?.setOnClickListener {
                startActivity(
                    Intent(
                        this@SearchActivity,
                        ShopDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.ShopParams.pack(item.id))
                    }
                )
                finish()
            }
            mItemBinding?.rvShopSearchPosition?.layoutManager = LinearLayoutManager(this)
            mItemBinding?.rvShopSearchPosition?.adapter =
                CommonRecyclerAdapter<ItemShopSearchSelectPositionBinding, ShopPositionSearch>(
                    R.layout.item_shop_search_select_position, BR.item
                ) { mPositionItemBinding, _, position ->
                    mPositionItemBinding?.root?.setOnClickListener {
                        position.id?.let { positionId ->
                            // 跳转到点位详情
                            startActivity(Intent(
                                this@SearchActivity,
                                ShopPositionDetailActivity::class.java
                            ).apply {
                                putExtras(IntentParams.CommonParams.pack(positionId))
                            })
                            finish()
                        }
                    }
                }.apply {
                    refreshList(item.positionList, true)
                }
        }
    }

    private val mDeviceAdapter: CommonRecyclerAdapter<ItemDeviceSearchSelectBinding, ISearchSelectEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_device_search_select,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@SearchActivity,
                        DeviceDetailActivity::class.java
                    ).apply {
                        putExtra(DeviceDetailActivity.GoodsId, item.getSearchId())
                    }
                )
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

    override fun onResume() {
        super.onResume()
        LiveDataBus.with(BusEvents.SCAN_CHANGE_STATUS, Boolean::class.java)?.observe(this) {
            startQRActivity(it)
        }
    }

    override fun onPause() {
        super.onPause()
        LiveDataBus.remove(BusEvents.SCAN_CHANGE_STATUS)
    }

    override fun initView() {
        mBinding.ibSearchBack.setOnClickListener {
            onBackListener()
        }
        mBinding.etSearchKey.onTextChange = { auto ->
            if (SearchType.Device == mViewModel.searchType || SearchType.Shop == mViewModel.searchType) {
                search(auto)
            } else if (SearchType.Order == mViewModel.searchType && !auto) {
                search(false)
            }
        }
        // 扫码
        mBinding.ibSearchScan.setOnClickListener {
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }
        mBinding.tvSearchSearch.setOnClickListener {
            search()
        }

        val childCount = mBinding.clSearchHistory.childCount
        if (childCount > 2) {
            mBinding.clSearchHistory.removeViews(1, childCount - 1)
        }
        val inflater = LayoutInflater.from(this)
        SPRepository.searchHistory?.let { history ->
            history.filter { item -> item.type == mViewModel.searchType }.reversed()
                .forEachIndexed { index, searchParam ->
                    DataBindingUtil.inflate<ItemSearchHistoryFlowBinding>(
                        inflater,
                        R.layout.item_search_history_flow, null, false
                    ).also { itemBinding ->
                        itemBinding.name = searchParam.keyword
                        itemBinding.root.id = index + 1
                        itemBinding.root.setOnClickListener {
                            mBinding.etSearchKey.clearFocus()
                            mViewModel.searchKey.value = searchParam.keyword.trim()
                            search(true)
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
            ContextCompat.getDrawable(this, R.drawable.divide_size8)?.let {
                mBinding.rvSearchList1.addItemDecoration(
                    CustomDividerItemDecoration(
                        this,
                        CustomDividerItemDecoration.VERTICAL,
                    ).apply {
                        setDrawable(it)
                    }
                )
            }
            mBinding.rvSearchList1.adapter = mShopAdapter
        } else {
            mBinding.rvSearchList2.enableRefresh = false
            mBinding.rvSearchList2.layoutManager = LinearLayoutManager(this)
            if (SearchType.Device == mViewModel.searchType) {
                mBinding.rvSearchList2.listStatusImgResId = R.mipmap.icon_list_device_empty
                mBinding.rvSearchList2.listStatusTxtResId = R.string.empty_device
                mBinding.rvSearchList2.adapter = mDeviceAdapter
            } else {
                mBinding.rvSearchList2.adapter = mAdapter
            }
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
                            mViewModel.searchList(page, pageSize, false, result2 = callBack)
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
            var list = SPRepository.searchHistory ?: mutableListOf()
            list.find { item -> item.type == mViewModel.searchType && item.keyword == keyword }
                ?.let { params ->
                    list.remove(params)
                }
            if (list.size >= 15){
                list = list.subList(list.size - 14,list.size)
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
                    searchShopList()
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

    private fun searchShopList() {
        if (SearchType.Shop == mViewModel.searchType) {
            mViewModel.searchShopList {
                mBinding.rvSearchList1.refreshData(it, true)
            }
        }
    }
}