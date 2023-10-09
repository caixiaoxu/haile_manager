package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StatusBarUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeShopStatisticsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ShopDeviceRevenueListEntity
import com.yunshang.haile_manager_android.data.entities.ShopRevenueDetailEntity
import com.yunshang.haile_manager_android.data.entities.UserFund
import com.yunshang.haile_manager_android.databinding.ActivityIncomeShopStatisticsBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeShopStatisticsListBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeStatisticsDeviceInfoBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeStatisticsSubAccountInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CustomDividerItemDecoration
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import com.yunshang.haile_manager_android.utils.span.VerticalBottomSpan
import java.util.*

class IncomeShopStatisticsActivity :
    BaseBusinessActivity<ActivityIncomeShopStatisticsBinding, IncomeShopStatisticsViewModel>(
        IncomeShopStatisticsViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                if (selected.isNotEmpty()) {
                                    if (selected.any { item -> 0 == item.id }) {
                                        mViewModel.shopIds = null
                                        mBinding.tvIncomeShopStatisticsShop.text = ""
                                    } else {
                                        mViewModel.shopIds = selected.map { item -> item.id }
                                        mBinding.tvIncomeShopStatisticsShop.text =
                                            if (1 == selected.size) selected.first().name else "${selected.size}家门店"
                                    }
                                    requestData()
                                }
                            }
                        }
                    }
                }
            }
        }

    private val mAdapter: CommonRecyclerAdapter<ItemIncomeShopStatisticsListBinding, ShopRevenueDetailEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_shop_statistics_list,
            BR.item
        ) { mItemBinding, _, item ->
            if (!item.userFundList.isNullOrEmpty()) {
                mItemBinding?.includeItemIncomeShopStatisticsSubAccount?.root?.visibility(true)
                mItemBinding?.includeItemIncomeShopStatisticsSubAccount?.llSubAccountInfo?.buildChild<ItemIncomeStatisticsSubAccountInfoBinding, UserFund>(
                    item.userFundList, LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DimensionUtils.dip2px(this@IncomeShopStatisticsActivity, 44f)
                    )
                ) { _, childBinding, data ->
                    childBinding.child = data
                }

                mItemBinding?.includeItemIncomeShopStatisticsSubAccount?.llSubAccountInfo?.visibility(
                    item.fold
                )
                mItemBinding?.includeItemIncomeShopStatisticsSubAccount?.tvSubAccountInfo?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (item.fold) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                    0
                )
                mItemBinding?.includeItemIncomeShopStatisticsSubAccount?.tvSubAccountInfo?.setOnClickListener {
                    item.fold = !item.fold
                    mItemBinding.includeItemIncomeShopStatisticsSubAccount.tvSubAccountInfo.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        if (item.fold) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                        0
                    )
                    mItemBinding.includeItemIncomeShopStatisticsSubAccount.llSubAccountInfo.visibility(
                        item.fold
                    )
                }
            } else mItemBinding?.includeItemIncomeShopStatisticsSubAccount?.root?.visibility(false)

            // 如果设备列表不为空，并且打开了设备列表，显示
            mItemBinding?.rvDeviceInfoList?.layoutManager =
                LinearLayoutManager(this@IncomeShopStatisticsActivity)
            ContextCompat.getDrawable(this, R.drawable.divder_efefef)?.let {
                mItemBinding?.rvDeviceInfoList?.addItemDecoration(
                    CustomDividerItemDecoration(this, CustomDividerItemDecoration.VERTICAL).apply {
                        setDrawable(it)
                    })
            }
            mItemBinding?.rvDeviceInfoList?.adapter =
                CommonRecyclerAdapter<ItemIncomeStatisticsDeviceInfoBinding, ShopDeviceRevenueListEntity>(
                    R.layout.item_income_statistics_device_info,
                    BR.device
                ) { mItemDeviceBinding, _, itemDevice ->
                    mItemDeviceBinding?.root?.setOnClickListener {
                        startActivity(
                            Intent(
                                this@IncomeShopStatisticsActivity,
                                IncomeExpensesDetailActivity::class.java
                            ).apply {
                                putExtras(
                                    IntentParams.ProfitStatisticsParams.pack(
                                        mViewModel.shopIds?.toIntArray(),
                                        mBinding.tvIncomeShopStatisticsShop.text.toString(),
                                        itemDevice.id,
                                        arrayOf(item.categoryCode),
                                        startTime = mViewModel.startDate.value,
                                        endTime = mViewModel.endDate.value,
                                    )
                                )
                            }
                        )
                    }
                }
            refreshDeviceList(mItemBinding, item)
            mItemBinding?.clDeviceInfoList?.visibility(
                !item.deviceList.isNullOrEmpty() && item.deviceFold
            )
            showLoadMore(mItemBinding, item)
            // 打开图标
            mItemBinding?.tvDeviceInfo?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (item.deviceFold) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                0
            )
            mItemBinding?.tvDeviceInfo?.setOnClickListener {
                if (!item.deviceFold && item.deviceList.isNullOrEmpty()) {
                    mViewModel.requestDeviceList(it.context, item) {
                        refreshDeviceList(mItemBinding, item)
                        showLoadMore(mItemBinding, item)
                        toggleDeviceList(mItemBinding, item)
                    }
                } else {
                    toggleDeviceList(mItemBinding, item)
                }
            }

            mItemBinding?.viewDeviceInfoListBottom?.setOnClickListener {
                mViewModel.requestDeviceList(it.context, item) {
                    refreshDeviceList(mItemBinding, item)
                    showLoadMore(mItemBinding, item)
                }
            }

            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@IncomeShopStatisticsActivity,
                        IncomeExpensesDetailActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.ProfitStatisticsParams.pack(
                                mViewModel.shopIds?.toIntArray(),
                                categoryCodes = arrayOf(item.categoryCode),
                                startTime = mViewModel.startDate.value,
                                endTime = mViewModel.endDate.value,
                            )
                        )
                    }
                )
            }
        }
    }

    private fun refreshDeviceList(
        mItemBinding: ItemIncomeShopStatisticsListBinding?, item: ShopRevenueDetailEntity,
    ) {
        (mItemBinding?.rvDeviceInfoList?.adapter as? CommonRecyclerAdapter<*, ShopDeviceRevenueListEntity>)?.refreshList(
            item.deviceList,
            true
        )
    }

    private fun showLoadMore(
        mItemBinding: ItemIncomeShopStatisticsListBinding?,
        item: ShopRevenueDetailEntity
    ) {
        mItemBinding?.groupDeviceInfoListBottom?.visibility =
            if (item.noMore) View.GONE else View.VISIBLE
    }

    /**
     * 切换设备列表显示
     */
    private fun toggleDeviceList(
        mItemBinding: ItemIncomeShopStatisticsListBinding,
        item: ShopRevenueDetailEntity
    ) {
        item.deviceFold = !item.deviceFold
        mItemBinding.tvDeviceInfo.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            if (item.deviceFold) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
            0
        )
        mItemBinding.clDeviceInfoList.visibility(item.deviceFold)
    }

    override fun isFullScreen(): Boolean = true

    override fun layoutId(): Int = R.layout.activity_income_shop_statistics

    override fun backBtn(): View = mBinding.barIncomeShopStatisticsTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopIds = IntentParams.ProfitStatisticsParams.parseShopIds(intent)?.toList()
        mBinding.tvIncomeShopStatisticsShop.text =
            IntentParams.ProfitStatisticsParams.parseShopName(intent) ?: ""

        IntentParams.ProfitStatisticsParams.parseStartTime(intent)?.let {
            mViewModel.startDate.value = it
        }
        IntentParams.ProfitStatisticsParams.parseEndTime(intent)?.let {
            mViewModel.endDate.value = it
        }
    }

    override fun initEvent() {
        super.initEvent()

        // 总收益
        mViewModel.totalRevenue.observe(this) {
            it?.let { total ->
                mBinding.tvIncomeShopStatisticsRevenue.text = StringUtils.formatMultiStyleStr(
                    "¥ ${total.revenue}", arrayOf(
                        VerticalBottomSpan(DimensionUtils.sp2px(24f).toFloat(), -3f)
                    ), 0, 2
                )
                if (!total.userFundList.isNullOrEmpty()) {
                    mBinding.includeIncomeShopStatisticsSubAccount.root.visibility(true)
                    mBinding.includeIncomeShopStatisticsSubAccount.llSubAccountInfo.buildChild<ItemIncomeStatisticsSubAccountInfoBinding, UserFund>(
                        total.userFundList, LinearLayoutCompat.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            DimensionUtils.dip2px(this@IncomeShopStatisticsActivity, 44f)
                        )
                    ) { _, childBinding, data ->
                        childBinding.child = data
                    }
                } else mBinding.includeIncomeShopStatisticsSubAccount.root.visibility(false)
            }
        }
    }

    override fun initView() {
        mBinding.llIncomeShopStatisticsTop.setPadding(0, StatusBarUtils.getStatusBarHeight(), 0, 0)

        if (UserPermissionUtils.hasProfitDetailPermission()) {
            mBinding.barIncomeShopStatisticsTitle.getRightBtn().run {
                setText(R.string.income_expenses_detail)
                setTextColor(
                    ContextCompat.getColor(
                        this@IncomeShopStatisticsActivity,
                        R.color.colorPrimary
                    )
                )
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@IncomeShopStatisticsActivity,
                            IncomeExpensesDetailActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.ProfitStatisticsParams.pack(
                                    mViewModel.shopIds?.toIntArray(),
                                    mBinding.tvIncomeShopStatisticsShop.text.toString(),
                                    startTime = mViewModel.startDate.value,
                                    endTime = mViewModel.endDate.value,
                                )
                            )
                        }
                    )
                }
            }
        }

        // 日期
        mBinding.tvIncomeShopStatisticsDate.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                selectModel = 1
                limitSpace = 31
                maxDate = Calendar.getInstance().apply { time = Date() }
                selectStartDate = mViewModel.startDate.value
                selectEndDate = mViewModel.endDate.value
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mViewModel.startDate.value = date1
                        mViewModel.endDate.value = date2
                        requestData()
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 店铺
        mBinding.tvIncomeShopStatisticsShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@IncomeShopStatisticsActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectStatisticsShop,
                            mustSelect = true,
                            selectArr = mViewModel.shopIds?.toIntArray() ?: intArrayOf(0)
                        )
                    )
                }
            )
        }

        // 设备
        mBinding.tvIncomeShopStatisticsCategory.setOnClickListener {
            MultiSelectBottomSheetDialog.Builder(
                getString(R.string.device_category),
                mViewModel.categoryList.value ?: listOf()
            ).apply {
                onValueSureListener =
                    object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                        override fun onValue(
                            selectData: List<CategoryEntity>,
                            allSelectData: List<CategoryEntity>
                        ) {
                            mViewModel.categoryCodes = selectData.mapNotNull { item -> item.code }
                            mBinding.tvIncomeShopStatisticsCategory.text =
                                if (1 == selectData.size) selectData.first().name else "${selectData.size}种设备"
                            requestData()
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 刷新加载
        mBinding.refreshLayout.setOnRefreshListener {
            requestData()
        }
        mBinding.refreshLayout.setEnableLoadMore(false)

        // 店铺列表
        mBinding.rvIncomeShopStatisticsList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(this, R.drawable.divide_size8)?.let {
            mBinding.rvIncomeShopStatisticsList.addItemDecoration(
                DividerItemDecoration(
                    this@IncomeShopStatisticsActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvIncomeShopStatisticsList.adapter = mAdapter
    }

    override fun initData() {
        requestData(0)
    }

    private fun requestData(type: Int = 1) {
        mViewModel.requestData(type) { shopDataList ->
            mBinding.refreshLayout.finishRefresh()
            mAdapter.refreshList(shopDataList, true)

            mBinding.tvIncomeShopStatisticsStatus.visibility(0 == mAdapter.itemCount)
        }
    }
}