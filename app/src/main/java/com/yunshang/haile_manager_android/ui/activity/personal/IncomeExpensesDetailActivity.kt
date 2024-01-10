package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeExpensesDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.IncomeExpensesDetailEntity
import com.yunshang.haile_manager_android.data.extend.formatMoney
import com.yunshang.haile_manager_android.databinding.ActivityIncomeExpensesDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeExpensesDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import com.yunshang.haile_manager_android.utils.span.VerticalBottomSpan
import java.util.*
import kotlin.math.abs

class IncomeExpensesDetailActivity :
    BaseBusinessActivity<ActivityIncomeExpensesDetailBinding, IncomeExpensesDetailViewModel>(
        IncomeExpensesDetailViewModel::class.java, BR.vm
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
                                        mBinding.tvIncomeExpensesDetailShop.text = ""
                                    } else {
                                        mViewModel.shopIds = selected.map { item -> item.id }
                                        mBinding.tvIncomeExpensesDetailShop.text =
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

    private val mAdapter: CommonRecyclerAdapter<ItemIncomeExpensesDetailBinding, IncomeExpensesDetailEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_expenses_detail,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                if (UserPermissionUtils.hasOrderInfoPermission()) {
                    startActivity(if ("1000" == item.categoryCode) {
                        Intent(
                            this@IncomeExpensesDetailActivity,
                            IncomeDetailActivity::class.java
                        ).apply {
                            putExtra(IncomeDetailActivity.DetailType, 2)
                            putExtra(IncomeDetailActivity.OrderNo, item.orderNo)
                        }
                    } else {
                        Intent(
                            this@IncomeExpensesDetailActivity,
                            OrderDetailActivity::class.java
                        ).apply {
                            putExtras(IntentParams.OrderDetailParams.pack(orderNo = item.orderNo))
                        }
                    })
                } else {
                    SToast.showToast(this@IncomeExpensesDetailActivity, R.string.no_permission)
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_income_expenses_detail

    override fun backBtn(): View = mBinding.barIncomeExpensesDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopIds = IntentParams.ProfitStatisticsParams.parseShopIds(intent)?.toList()
        mBinding.tvIncomeExpensesDetailShop.text =
            IntentParams.ProfitStatisticsParams.parseShopName(intent) ?: ""
        mViewModel.categoryCodes =
            IntentParams.ProfitStatisticsParams.parseCategoryCodes(intent)?.toList()
        mViewModel.goodsId = IntentParams.ProfitStatisticsParams.parseGoodId(intent)
        mViewModel.formType = IntentParams.ProfitStatisticsParams.parseFormType(intent)

        IntentParams.ProfitStatisticsParams.parseStartTime(intent)?.let {
            mViewModel.startDate.value = it
        }
        IntentParams.ProfitStatisticsParams.parseEndTime(intent)?.let {
            mViewModel.endDate.value = it
        }
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.totalRevenue.observe(this) {
            it?.let { total ->
                mBinding.tvIncomeExpensesDetailTotal.text = StringUtils.formatMultiStyleStr(
                    "¥ ${total.revenue.formatMoney()}", arrayOf(
                        VerticalBottomSpan(DimensionUtils.sp2px(24f).toFloat(), -3f)
                    ), 0, 2
                )
                mBinding.tvIncomeExpensesDetailTotalCategory.text =
                    "总收入 ¥${total.income.formatMoney()}      总支出 ¥${total.expend.formatMoney()}"
            }
        }
    }

    override fun initView() {
        mBinding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if ((appBarLayout.height - DimensionUtils.dip2px(
                    this,
                    8f
                )) - mBinding.rgIncomeExpensesDetailType.height - abs(verticalOffset) < 10
            ) {
                window.statusBarColor = Color.WHITE
                mBinding.root.setBackgroundColor(Color.WHITE)
            } else {
                window.statusBarColor = Color.TRANSPARENT
                mBinding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        this@IncomeExpensesDetailActivity,
                        R.color.page_bg
                    )
                )
            }
        }
        // 日期
        mBinding.tvIncomeExpensesDetailDate.setOnClickListener {
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
                        requestData(true)
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 店铺
        mBinding.tvIncomeExpensesDetailShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@IncomeExpensesDetailActivity,
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
        mBinding.tvIncomeExpensesDetailCategory.setOnClickListener {
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
                            mBinding.tvIncomeExpensesDetailCategory.text =
                                if (1 == selectData.size) selectData.first().name else "${selectData.size}种设备"
                            requestData(true)
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 收支类型
        mBinding.rgIncomeExpensesDetailType.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_income_expenses_detail_type_all -> mViewModel.transactionType = null
                R.id.rb_income_expenses_detail_type_earning -> mViewModel.transactionType = 1
                R.id.rb_income_expenses_detail_type_expend -> mViewModel.transactionType = 2
            }
            requestData(true)
        }

        // 刷新加载
        mBinding.refreshLayout.setOnRefreshListener {
            requestData(true)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            requestData()
        }

        // 明细列表
        mBinding.rvIncomeExpensesDetailList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(this, R.drawable.divide_size8)?.let {
            mBinding.rvIncomeExpensesDetailList.addItemDecoration(
                DividerItemDecoration(
                    this@IncomeExpensesDetailActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvIncomeExpensesDetailList.adapter = mAdapter
    }

    override fun initData() {
        requestData(true, 0)
    }

    private fun requestData(isRefresh: Boolean = false, type: Int = 1) {
        if (isRefresh) {
            mBinding.refreshLayout.setEnableLoadMore(true)
        }

        mViewModel.requestData(type, isRefresh) { list ->
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            mAdapter.refreshList(list, isRefresh)
            if (list.isNullOrEmpty()) {
                mBinding.refreshLayout.setEnableLoadMore(false)
            }
            mBinding.tvIncomeExpensesDetailListStatus.visibility(0 == mAdapter.itemCount)
        }
    }
}