package com.yunshang.haile_manager_android.ui.activity.statistics

import android.content.Intent
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DataStatisticsDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.databinding.ActivityDataStatisticsDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

class DataStatisticsDetailActivity :
    BaseBusinessActivity<ActivityDataStatisticsDetailBinding, DataStatisticsDetailViewModel>(
        DataStatisticsDetailViewModel::class.java,
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                mViewModel.selectDepartment.value =
                                    if (selected.isNotEmpty()) selected[0] else null
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_data_statistics_detail

    override fun backBtn(): View = mBinding.barDataStatisticsDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        val shopId = IntentParams.ShopParams.parseShopId(intent)
        val shopName = IntentParams.ShopParams.parseShopName(intent)
        if (-1 != shopId && !shopName.isNullOrEmpty()) {
            mViewModel.selectDepartment.value = SearchSelectParam(shopId, shopName)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.dateVal.observe(this) {
            mBinding.includeDataStatisticsDetailFilter.tvDataStatisticsTime.text = it
        }

        mViewModel.selectDepartment.observe(this) {
            mBinding.includeDataStatisticsDetailFilter.tvDataStatisticsShop.text = it?.name ?: ""
            requestData()
        }

        // 设备类型
        mViewModel.categoryList.observe(this) {
            showDeviceCategoryDialog(it)
        }

        // 选择设备类型
        mViewModel.selectDeviceCategory.observe(this) {
            mBinding.includeDataStatisticsDetailFilter.tvDataStatisticsDevice.text = it?.name ?: ""
            requestData()
        }
        
        mViewModel.statisticsShopDetail.observe(this){
            it?.let {detail->
                // 订单数据
                detail.orderStatisticsVO?.let { orderStatistics ->
                    mBinding.includeDataStatisticsDetailOrder.root.visibility = View.VISIBLE
                    (mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.viewColumn1.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
                        DimensionUtils.dip2px(this@DataStatisticsDetailActivity, 24f)
                    (mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.viewColumn3.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                        DimensionUtils.dip2px(this@DataStatisticsDetailActivity, 24f)
                    mBinding.includeDataStatisticsDetailOrder.tvDataStatisticsDetailItemTitle.text =
                        "订单数据"
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsOrderNumTitle,
                        "订单量",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsOrderNum,
                        orderStatistics.totalCount,
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsOrderNumTrend,
                        orderStatistics.totalCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsRevenueTitle,
                        "收益",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsRevenue,
                        orderStatistics.orderRevenueAmount,
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsRevenueTrend,
                        orderStatistics.orderRevenueAmountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsActiveUserTitle,
                        "无效订单量",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsActiveUser,
                        orderStatistics.orderInvalidCount,
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsActiveUserTrend,
                        orderStatistics.orderInvalidCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsAddUserTitle,
                        "退款订单量",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsAddUser,
                        orderStatistics.orderRefundCount,
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsAddUserTrend,
                        orderStatistics.orderRefundCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsActiveDeviceTitle,
                        "退款金额",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsActiveDevice,
                        orderStatistics.orderRefundAmount,
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsActiveDeviceTrend,
                        orderStatistics.orderRefundAmountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequencyTitle,
                        "失效订单占比",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequency,
                        orderStatistics.orderInvalidRate + "%",
                        mBinding.includeDataStatisticsDetailOrder.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequencyTrend,
                        orderStatistics.orderInvalidRateCompare
                    )
                } ?: run {
                    mBinding.includeDataStatisticsDetailOrder.root.visibility = View.GONE
                }

                // 设备数据
                detail.deviceStatisticsVO?.let { deviceStatistics ->
                    mBinding.includeDataStatisticsDetailDevice.root.visibility =
                        View.VISIBLE
                    (mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.viewColumn1.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
                        DimensionUtils.dip2px(this@DataStatisticsDetailActivity, 24f)
                    (mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.viewColumn3.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                        DimensionUtils.dip2px(this@DataStatisticsDetailActivity, 24f)
                    mBinding.includeDataStatisticsDetailDevice.tvDataStatisticsDetailItemTitle.text =
                        "设备数据"
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsOrderNumTitle,
                        "设备数量",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsOrderNum,
                        deviceStatistics.deviceTotalCount,
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsOrderNumTrend,
                        deviceStatistics.deviceTotalCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsRevenueTitle,
                        "活跃设备量",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsRevenue,
                        deviceStatistics.deviceActiveCount,
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsRevenueTrend,
                        deviceStatistics.deviceActiveCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsActiveUserTitle,
                        "设备活跃率",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsActiveUser,
                        deviceStatistics.deviceActiveRate + "%",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsActiveUserTrend,
                        deviceStatistics.deviceActiveRateCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsAddUserTitle,
                        "设备故障率",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsAddUser,
                        deviceStatistics.deviceFaultRate + "%",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsAddUserTrend,
                        deviceStatistics.deviceFaultRateCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsActiveDeviceTitle,
                        "设备离线率",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsActiveDevice,
                        deviceStatistics.deviceOfflineRate + "%",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsActiveDeviceTrend,
                        deviceStatistics.deviceOfflineRateCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequencyTitle,
                        "设备频次",
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequency,
                        deviceStatistics.deviceUseFrequency,
                        mBinding.includeDataStatisticsDetailDevice.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequencyTrend,
                        deviceStatistics.deviceUseFrequencyCompare
                    )
                } ?: run {
                    mBinding.includeDataStatisticsDetailDevice.root.visibility = View.GONE
                }

                //用户数据
                detail.userStatisticsVO?.let { userStatistics ->
                    mBinding.includeDataStatisticsDetailUser.root.visibility = View.VISIBLE
                    (mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.viewColumn1.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
                        DimensionUtils.dip2px(this@DataStatisticsDetailActivity, 24f)
                    (mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.viewColumn3.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                        DimensionUtils.dip2px(this@DataStatisticsDetailActivity, 24f)
                    mBinding.includeDataStatisticsDetailUser.tvDataStatisticsDetailItemTitle.text =
                        "用户数据"
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsOrderNumTitle,
                        "总用户量",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsOrderNum,
                        userStatistics.userTotalCount,
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsOrderNumTrend,
                        userStatistics.userTotalCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsRevenueTitle,
                        "新增用户量",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsRevenue,
                        userStatistics.userAddCount,
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsRevenueTrend,
                        userStatistics.userAddCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsActiveUserTitle,
                        "新用户占比",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsActiveUser,
                        userStatistics.userAddPercent + "%",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsActiveUserTrend,
                        userStatistics.userAddPercentCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsAddUserTitle,
                        "活跃用户量",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsAddUser,
                        userStatistics.userActiveCount,
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsAddUserTrend,
                        userStatistics.userActiveCountCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsActiveDeviceTitle,
                        "用户活跃率",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsActiveDevice,
                        userStatistics.userActivePercent + "%",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsActiveDeviceTrend,
                        userStatistics.userActivePercentCompare
                    )
                    refreshItemView(
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequencyTitle,
                        "用户复购率",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequency,
                        userStatistics.userRepeatBuyPercent + "%",
                        mBinding.includeDataStatisticsDetailUser.includeDataStatisticsDetailItems.tvDataStatisticsDeviceFrequencyTrend,
                        userStatistics.userRepeatBuyPercentCompare
                    )
                } ?: run {
                    mBinding.includeDataStatisticsDetailUser.root.visibility = View.GONE
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.includeDataStatisticsDetailDate.rgDataStatisticsCategoryDate.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_data_statistics_category_day -> mViewModel.dateType.value = 1
                R.id.rb_data_statistics_category_week -> mViewModel.dateType.value = 2
                R.id.rb_data_statistics_category_month -> mViewModel.dateType.value = 3
                R.id.rb_data_statistics_category_year -> mViewModel.dateType.value = 4
            }
        }

        mBinding.includeDataStatisticsDetailFilter.tvDataStatisticsTime.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                selectModel = if (1 == mViewModel.dateType.value) 1 else 0
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
                            1 -> {
                                mViewModel.startTime.value = date1
                                date2?.let {
                                    mViewModel.endTime.value = date2
                                }
                            }
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
                        requestData()
                    }
                }
            }.build().show(
                supportFragmentManager,
                when (mViewModel.dateType.value) {
                    1 -> mViewModel.startTime.value
                    2 -> mViewModel.startWeekTime.value
                    else -> mViewModel.singleTime.value
                },
                if (1 == mViewModel.dateType.value) mViewModel.endTime.value else null
            )
        }

        // 店铺
        mBinding.includeDataStatisticsDetailFilter.tvDataStatisticsShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DataStatisticsDetailActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                        )
                    )
                }
            )
        }

        // 设备类型
        mBinding.includeDataStatisticsDetailFilter.tvDataStatisticsDevice.setOnClickListener {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mViewModel.requestDeviceCategory()
        }

        mBinding.includeDataStatisticsDetailDate.root.setBackgroundColor(Color.WHITE)

        mBinding.includeDataStatisticsDetailOrder.tvDataStatisticsDetailItemHint.setOnClickListener {
            showHintDialog()
        }
        mBinding.includeDataStatisticsDetailDevice.tvDataStatisticsDetailItemHint.setOnClickListener {
            showHintDialog()
        }
        mBinding.includeDataStatisticsDetailUser.tvDataStatisticsDetailItemHint.setOnClickListener {
            showHintDialog()
        }
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
                            }
                        }
                }
                .build()
        deviceCategoryDialog.show(supportFragmentManager)
    }

    private fun showHintDialog() {
        CommonDialog.Builder(StringUtils.getString(R.string.indicator_specification_content))
            .apply {
                title = StringUtils.getString(R.string.indicator_specification)
                isNegativeShow = false
                positiveTxt = StringUtils.getString(R.string.i_know)
            }.build().show(supportFragmentManager)
    }

    private fun refreshItemView(
        tvTitle: AppCompatTextView?,
        title: String,
        tv: AppCompatTextView?,
        value: String,
        tvTrend: AppCompatTextView?,
        trend: Double
    ) {
        tvTitle?.text = title
        tv?.text = value
        tvTrend?.text =
            "环比${com.yunshang.haile_manager_android.utils.StringUtils.formatNumberStr(trend)}%".let {
                com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    it,
                    arrayOf(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@DataStatisticsDetailActivity,
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
    }

    override fun initData() {
        requestData()
    }

    private fun requestData() {
        mViewModel.requestShopDetailData()
    }
}