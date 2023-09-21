package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeExpensesDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.IncomeExpensesDetailEntity
import com.yunshang.haile_manager_android.databinding.ActivityIncomeExpensesDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeExpensesDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.span.VerticalBottomSpan
import java.util.*

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
                startActivity(
                    Intent(
                        this@IncomeExpensesDetailActivity,
                        OrderDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.OrderDetailParams.pack(item.orderId))
                    })
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_income_expenses_detail

    override fun backBtn(): View = mBinding.barIncomeExpensesDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopIds = IntentParams.ProfitStatisticsParams.parseShopIds(intent)?.toList()
        mViewModel.categoryCodes =
            IntentParams.ProfitStatisticsParams.parseCategoryCodes(intent)?.toList()
        mViewModel.goodsId = IntentParams.ProfitStatisticsParams.parseGoodId(intent)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.totalRevenue.observe(this) {
            it?.let { total ->
                mBinding.tvIncomeExpensesDetailTotal.text = StringUtils.formatMultiStyleStr(
                    "¥ ${total.revenue}", arrayOf(
                        VerticalBottomSpan(DimensionUtils.sp2px(24f).toFloat(), -3f)
                    ), 0, 2
                )
                mBinding.tvIncomeExpensesDetailTotalCategory.text =
                    "总收入 ¥${total.income}      总收支出 ¥${total.expend}"
            }
        }
    }

    override fun initView() {

        // 日期
        mBinding.tvIncomeExpensesDetailDate.setOnClickListener {
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
        }
    }
}