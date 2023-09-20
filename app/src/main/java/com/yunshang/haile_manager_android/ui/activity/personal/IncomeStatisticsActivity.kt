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
import com.yunshang.haile_manager_android.business.vm.IncomeStatisticsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ShopRevenueEntity
import com.yunshang.haile_manager_android.data.entities.UserFund
import com.yunshang.haile_manager_android.databinding.ActivityIncomeStatisticsBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeStatisticsShopBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeStatisticsSubAccountInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import com.yunshang.haile_manager_android.utils.span.VerticalBottomSpan
import java.util.*

class IncomeStatisticsActivity :
    BaseBusinessActivity<ActivityIncomeStatisticsBinding, IncomeStatisticsViewModel>(
        IncomeStatisticsViewModel::class.java, BR.vm
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
                                        mBinding.tvIncomeStatisticsShop.text = ""
                                    } else {
                                        mViewModel.shopIds = selected.map { item -> item.id }
                                        mBinding.tvIncomeStatisticsShop.text =
                                            if (1 == selected.size) selected.first().name else "${selected.size}家门店"
                                    }
                                    requestData(true)
                                }
                            }
                        }
                    }
                }
            }
        }

    private val mAdapter: CommonRecyclerAdapter<ItemIncomeStatisticsShopBinding, ShopRevenueEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_statistics_shop,
            BR.item
        ) { mItemBinding, _, item ->
            if (!item.userFundList.isNullOrEmpty()) {
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.root?.visibility(true)
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.llSubAccountInfo?.buildChild<ItemIncomeStatisticsSubAccountInfoBinding, UserFund>(
                    item.userFundList, LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DimensionUtils.dip2px(this@IncomeStatisticsActivity, 44f)
                    )
                ) { _, childBinding, data ->
                    childBinding.child = data
                }
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.llSubAccountInfo?.visibility(
                    item.fold
                )
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.tvSubAccountInfo?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (item.fold) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                    0
                )
                mItemBinding?.includeItemIncomeStatisticsSubAccount?.tvSubAccountInfo?.setOnClickListener {
                    item.fold = !item.fold
                    mItemBinding.includeItemIncomeStatisticsSubAccount.tvSubAccountInfo.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        if (item.fold) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                        0
                    )
                    mItemBinding.includeItemIncomeStatisticsSubAccount.llSubAccountInfo.visibility(
                        item.fold
                    )
                }
            } else mItemBinding?.includeItemIncomeStatisticsSubAccount?.root?.visibility(false)

            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@IncomeStatisticsActivity,
                        IncomeShopStatisticsActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.ShopParams.packShops(
                                intArrayOf(item.shopId),
                                item.shopName
                            )
                        )
                    })
            }
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
                    "¥ ${total.revenue}", arrayOf(
                        VerticalBottomSpan(DimensionUtils.sp2px(24f).toFloat(), -3f)
                    ), 0, 2
                )
                if (!total.userFundList.isNullOrEmpty()) {
                    mBinding.includeIncomeStatisticsSubAccount.root.visibility(true)
                    mBinding.includeIncomeStatisticsSubAccount.llSubAccountInfo.buildChild<ItemIncomeStatisticsSubAccountInfoBinding, UserFund>(
                        total.userFundList, LinearLayoutCompat.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            DimensionUtils.dip2px(this@IncomeStatisticsActivity, 44f)
                        )
                    ) { _, childBinding, data ->
                        childBinding.child = data
                    }
                } else mBinding.includeIncomeStatisticsSubAccount.root.visibility(false)
            }
        }
    }

    override fun initView() {
        mBinding.llIncomeStatisticsTop.setPadding(0, StatusBarUtils.getStatusBarHeight(), 0, 0)

        if (UserPermissionUtils.hasProfitDetailPermission()) {
            mBinding.barIncomeStatisticsTitle.getRightBtn().run {
                setText(R.string.income_expenses_detail)
                setTextColor(
                    ContextCompat.getColor(
                        this@IncomeStatisticsActivity,
                        R.color.colorPrimary
                    )
                )
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@IncomeStatisticsActivity,
                            IncomeExpensesDetailActivity::class.java
                        )
                    )
                }
            }
        }

        // 日期
        mBinding.tvIncomeStatisticsDate.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                selectModel = 1
                limitSpace = 31
                maxDate = Calendar.getInstance().apply { time = Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mViewModel.startDate.value = date1
                        mViewModel.endDate.value = date1
                        requestData(true)
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 店铺
        mBinding.tvIncomeStatisticsShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@IncomeStatisticsActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectStatisticsShop,
                            mustSelect = true,
                            moreSelect = true,
                            hasAll = true,
                            selectArr = mViewModel.shopIds?.toIntArray() ?: intArrayOf(0)
                        )
                    )
                }
            )
        }

        // 设备
        mBinding.tvIncomeStatisticsCategory.setOnClickListener {
            MultiSelectBottomSheetDialog.Builder(
                getString(R.string.coupon_device_dialog_title),
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
                            mBinding.tvIncomeStatisticsCategory.text =
                                if (1 == selectData.size) selectData.first().name else "${selectData.size}种设备"
                            requestData(true)
                        }
                    }
            }.build().show(supportFragmentManager)
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
        requestData(true, 0)
    }

    private fun requestData(isRefresh: Boolean = false, type: Int = 1) {
        if (isRefresh) {
            mBinding.refreshLayout.setEnableLoadMore(true)
        }

        mViewModel.requestData(type, isRefresh) { shopDataList ->
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            mAdapter.refreshList(shopDataList, isRefresh)
            if (shopDataList.isNullOrEmpty()) {
                mBinding.refreshLayout.setEnableLoadMore(false)
            }
        }
    }
}