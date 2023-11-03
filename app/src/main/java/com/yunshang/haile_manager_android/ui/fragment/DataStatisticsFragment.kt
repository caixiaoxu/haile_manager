package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StatusBarUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DataStatisticsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DataStatisticsShopListEntity
import com.yunshang.haile_manager_android.databinding.FragmentDataStatisticsBinding
import com.yunshang.haile_manager_android.databinding.ItemDataStatisticsBinding
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.statistics.DataStatisticsDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/20 15:37
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DataStatisticsFragment :
    BaseBusinessFragment<FragmentDataStatisticsBinding, DataStatisticsViewModel>(
        DataStatisticsViewModel::class.java,
        BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDataStatisticsBinding, DataStatisticsShopListEntity>(
            R.layout.item_data_statistics,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.let {
                // 总收益
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsTotalRevenue,
                    item.revenue,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsTotalRevenueTrend,
                    item.revenueCompare, true
                )
                // 总收入
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsTotalEarnings,
                    item.income,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsTotalEarningsTrend,
                    item.incomeCompare, true
                )
                // 总支出
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsTotalExpend,
                    item.expend,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsTotalExpendTrend,
                    item.expendCompare, true
                )
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsOrderNum,
                    item.orderTotalCount,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsOrderNumTrend,
                    item.orderTotalCountCompare
                )
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsStartOrder,
                    item.deviceOrderCount,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsStartOrderTrend,
                    item.deviceOrderCountCompare
                )
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsActiveUser,
                    item.userActiveCount,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsActiveUserTrend,
                    item.userActiveCountCompare
                )
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsAddUser,
                    item.userAddCount,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsAddUserTrend,
                    item.userAddCountCompare
                )
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsActiveDevice,
                    item.deviceActiveCount,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsActiveDeviceTrend,
                    item.deviceActiveCountCompare
                )
                refreshItemView(
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsDeviceFrequency,
                    item.deviceUseFrequency,
                    mItemBinding.includeDataStatisticsItems.tvDataStatisticsDeviceFrequencyTrend,
                    item.deviceUseFrequencyCompare
                )
            }
            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        DataStatisticsDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.ShopParams.pack(item.shopId, item.shopName))
                    })
            }
        }
    }

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                mViewModel.selectDepartments.value = selected
                                requestData(true)
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.fragment_data_statistics

    override fun initEvent() {
        super.initEvent()

        mViewModel.dateVal.observe(this) {
            mBinding.includeDataStatisticsFilter.tvDataStatisticsTime.text = it
        }

        mViewModel.selectDepartments.observe(this) {
            mBinding.includeDataStatisticsFilter.tvDataStatisticsShop.text =
                when (val count: Int? = it?.size) {
                    null, 0 -> ""
                    1 -> it.firstOrNull()?.name ?: ""
                    else -> "${count}家门店"
                }
        }

        // 设备类型
        mViewModel.categoryList.observe(this) {
            showDeviceCategoryDialog(it)
        }

        // 选择设备类型
        mViewModel.selectDeviceCategory.observe(this) {
            mBinding.includeDataStatisticsFilter.tvDataStatisticsDevice.text = it?.name ?: ""
        }

        // 总数据
        mViewModel.statisticsTotal.observe(this) {
            it?.let { total ->
                // 总收益
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsTotalRevenue,
                    total.revenue,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsTotalRevenueTrend,
                    total.revenueCompare, true
                )
                // 总收入
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsTotalEarnings,
                    total.income,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsTotalEarningsTrend,
                    total.incomeCompare, true
                )
                // 总支出
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsTotalExpend,
                    total.expend,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsTotalExpendTrend,
                    total.expendCompare, true
                )
                // 订单量
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsOrderNum,
                    total.orderTotalCount,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsOrderNumTrend,
                    total.orderTotalCountCompare, true
                )
                // 启动订单
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsStartOrder,
                    total.deviceOrderCount,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsStartOrderTrend,
                    total.deviceOrderCountCompare, true
                )
                // 活跃用户量
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsActiveUser,
                    total.userActiveCount,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsActiveUserTrend,
                    total.userActiveCountCompare, true
                )
                // 新增用户量
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsAddUser,
                    total.userAddCount,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsAddUserTrend,
                    total.userAddCountCompare, true
                )
                // 活跃设备数
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsActiveDevice,
                    total.deviceActiveCount,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsActiveDeviceTrend,
                    total.deviceActiveCountCompare, true
                )
                // 设备频次
                refreshItemView(
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsDeviceFrequency,
                    total.deviceUseFrequency,
                    mBinding.includeDataStatisticsTotal.tvDataStatisticsDeviceFrequencyTrend,
                    total.deviceUseFrequencyCompare, true
                )
            }
        }
    }

    override fun initView() {
        mBinding.root.setPadding(0, StatusBarUtils.getStatusBarHeight(), 0, 0)

        // 日、周、月、年
        mBinding.includeDataStatisticsDate.rgDataStatisticsCategoryDate.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_data_statistics_category_day -> mViewModel.dateType.value = 1
                R.id.rb_data_statistics_category_week -> mViewModel.dateType.value = 2
                R.id.rb_data_statistics_category_month -> mViewModel.dateType.value = 3
                R.id.rb_data_statistics_category_year -> mViewModel.dateType.value = 4
            }
            requestData(true)
        }

        // 日期选择
        mBinding.includeDataStatisticsFilter.tvDataStatisticsTime.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                selectModel = 0
                showModel = when (mViewModel.dateType.value) {
                    2 -> 7
                    3 -> 1
                    4 -> 6
                    else -> 0
                }
                maxDate = Calendar.getInstance().apply { time = DateTimeUtils.beforeDay(Date(), 1) }
                limitSpace = 31
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        when (mViewModel.dateType.value) {
                            2 -> {
                                mViewModel.startWeekTime.value = date1
                                date2?.let {
                                    mViewModel.endWeekTime.value = date2
                                }
                            }
                            else -> {
                                mViewModel.singleTime.value = date1
                            }
                        }
                        requestData(true)
                    }
                }
            }.build().show(
                childFragmentManager,
                when (mViewModel.dateType.value) {
                    2 -> mViewModel.startWeekTime.value
                    else -> mViewModel.singleTime.value
                }
            )
        }

        // 店铺
        mBinding.includeDataStatisticsFilter.tvDataStatisticsShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    requireContext(),
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectStatisticsShop,
                            mustSelect = false,
                            moreSelect = true,
                            selectArr = mViewModel.selectDepartments.value?.map { item -> item.id }
                                ?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }

        // 设备类型
        mBinding.includeDataStatisticsFilter.tvDataStatisticsDevice.setOnClickListener {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mViewModel.requestDeviceCategory()
        }

        mBinding.includeDataStatisticsTotal.root.setBackgroundColor(Color.WHITE)
        (mBinding.includeDataStatisticsTotal.viewColumn1.layoutParams as ConstraintLayout.LayoutParams).topMargin =
            DimensionUtils.dip2px(requireContext(), 24f)

        (mBinding.includeDataStatisticsTotal.viewColumn3.layoutParams as ConstraintLayout.LayoutParams).let {
            it.topMargin = DimensionUtils.dip2px(requireContext(), 18f)
            it.bottomMargin = DimensionUtils.dip2px(requireContext(), 18f)
        }

        mBinding.refreshLayout.setOnRefreshListener {
            requestData(true)
        }

        mBinding.refreshLayout.setOnLoadMoreListener {
            requestData()
        }

        mBinding.rvDataStatisticsList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvDataStatisticsList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvDataStatisticsList.adapter = mAdapter
    }

    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceCategoryDialog(categoryEntities: List<CategoryEntity>) {
        val deviceCategoryDialog =
            CommonBottomSheetDialog.Builder(getString(R.string.device_category), categoryEntities)
                .apply {
                    mustSelect = false
                    onValueSureListener =
                        object : CommonBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                            override fun onValue(data: CategoryEntity?) {
                                mViewModel.selectDeviceCategory.value = data
                                requestData(true)
                            }
                        }
                }
                .build()
        deviceCategoryDialog.show(childFragmentManager)
    }

    private fun refreshItemView(
        tv: AppCompatTextView?,
        value: String,
        tvTrend: AppCompatTextView?,
        trend: Double,
        showTrendIcon: Boolean = false
    ) {
        tv?.text = value
        tvTrend?.text = "环比${StringUtils.formatNumberStr(trend)}%".let {
            StringUtils.formatMultiStyleStr(
                it,
                arrayOf(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            if (trend > 0.0)
                                R.color.colorPrimary
                            else if (trend < 0.0) {
                                R.color.color_green
                            } else {
                                R.color.common_sub_txt_color
                            }
                        )
                    ),
                ),
                2, it.length,
            )
        }
        if (showTrendIcon)
            tvTrend?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (trend > 0) R.mipmap.icon_revenue_increase else if (trend < 0) R.mipmap.icon_revenue_decline else 0,
                0
            )
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
            if (shopDataList.isNullOrEmpty()) {
                mBinding.refreshLayout.setEnableLoadMore(false)
            } else {
                mAdapter.refreshList(shopDataList, isRefresh)
            }
        }
    }
}