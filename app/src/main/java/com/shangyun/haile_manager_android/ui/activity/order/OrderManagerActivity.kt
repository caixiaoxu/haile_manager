package com.shangyun.haile_manager_android.ui.activity.order

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.OrderManagerViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.arguments.SearchType
import com.shangyun.haile_manager_android.data.entities.OrderListEntity
import com.shangyun.haile_manager_android.databinding.ActivityOrderManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemOrderListBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import com.shangyun.haile_manager_android.utils.NumberUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber
import java.util.*

class OrderManagerActivity :
    BaseBusinessActivity<ActivityOrderManagerBinding, OrderManagerViewModel>() {

    private val mOrderManagerViewModel by lazy {
        getActivityViewModelProvider(this)[OrderManagerViewModel::class.java]
    }

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                    GsonUtils.json2Class(json, SearchSelectParam::class.java)
                        ?.let { selected ->
                            when (it.resultCode) {
                                SearchSelectRadioActivity.ShopResultCode -> {
                                    mOrderManagerViewModel.selectDepartment.value = selected
                                }
                            }
                        }
                }
            }
        }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemOrderListBinding, OrderListEntity>(
            R.layout.item_order_list,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.llOrderListSpecs?.removeAllViews()
            item.skuList.forEach { sku ->
                mItemBinding?.llOrderListSpecs?.addView(
                    TextView(this@OrderManagerActivity).apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this@OrderManagerActivity,
                                R.color.colorPrimary
                            )
                        )
                        text = StringUtils.getString(
                            R.string.order_specs,
                            sku.skuName,
                            sku.skuUnit,
                            NumberUtils.keepTwoDecimals(sku.originUnitPrice)
                        )
                    }, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }

    private val dateDialog by lazy {
        DateSelectorDialog.Builder().apply {
            selectModel = 1
            maxDate = Calendar.getInstance().apply { time = Date() }
            onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                    Timber.i("----选择的开始日期${DateTimeUtils.formatDateTime("yyyy-MM-dd", date1)}")
                    Timber.i("----选择的结束日期${DateTimeUtils.formatDateTime("yyyy-MM-dd", date2)}")
                    //更换时间
                    mOrderManagerViewModel.startTime.value = date1
                    mOrderManagerViewModel.endTime.value = date2

                    mBinding.rvOrderManagerList.requestRefresh()
                }
            }
        }.build()
    }

    override fun layoutId(): Int = R.layout.activity_order_manager

    override fun getVM(): OrderManagerViewModel = mOrderManagerViewModel

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasOrderListPermission.observe(this) {}
        mSharedViewModel.hasOrderInfoPermission.observe(this) {}

        // 刷新状态
        mOrderManagerViewModel.orderStatus.observe(this) { list ->
            mBinding.indicatorOrderStatus.navigator = CommonNavigator(this).apply {

                adapter = object : CommonNavigatorAdapter() {
                    override fun getCount(): Int = list.size

                    override fun getTitleView(context: Context?, index: Int): IPagerTitleView? {
                        return SimplePagerTitleView(context).apply {
                            normalColor = Color.parseColor("#666666")
                            selectedColor = Color.WHITE
                            list[index].run {
                                text = title
                                setOnClickListener {
                                    mOrderManagerViewModel.curOrderStatus.value = value
                                    onPageSelected(index)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    override fun getIndicator(context: Context?): IPagerIndicator? {
                        return WrapPagerIndicator(context).apply {
                            verticalPadding = DimensionUtils.dip2px(this@OrderManagerActivity, 4f)
                            fillColor = ContextCompat.getColor(
                                this@OrderManagerActivity,
                                R.color.colorPrimary
                            )
                            roundRadius =
                                DimensionUtils.dip2px(this@OrderManagerActivity, 14f).toFloat()
                        }
                    }
                }
            }
        }

        // 切换工作状态
        mOrderManagerViewModel.curOrderStatus.observe(this) {
            mBinding.rvOrderManagerList.requestRefresh()
        }

        // 选择店铺
        mOrderManagerViewModel.selectDepartment.observe(this) {
            mBinding.tvOrderCategoryDepartment.text = it.name
            mBinding.rvOrderManagerList.requestRefresh()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.viewOrderManagerSearchBg.setOnClickListener {
            startActivity(Intent(this@OrderManagerActivity, SearchActivity::class.java).apply {
                putExtra(SearchType.SearchType, SearchType.Order)
            })
        }
        mBinding.tvOrderCategoryTime.setOnClickListener {

            dateDialog.show(
                supportFragmentManager,
                mOrderManagerViewModel.startTime.value,
                mOrderManagerViewModel.endTime.value
            )
        }

        // 所属门店
        mBinding.tvOrderCategoryDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@OrderManagerActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtra(
                        SearchSelectRadioActivity.SearchSelectType,
                        SearchSelectRadioViewModel.SearchSelectTypeShop
                    )
                }
            )
        }

        // 刷新
        mBinding.tvOrderManagerListRefresh.setOnClickListener {
            mBinding.rvOrderManagerList.requestRefresh()
        }

        mBinding.rvOrderManagerList.layoutManager = LinearLayoutManager(this)

        mBinding.rvOrderManagerList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.shape_height_8, null)?.let {
                    setDrawable(it)
                }
            })

        mBinding.rvOrderManagerList.adapter = mAdapter

        mBinding.rvOrderManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<OrderListEntity>() {
                override fun requestData(
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out OrderListEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasOrderListPermission.value) {
                        mOrderManagerViewModel.requestOrderList(page, pageSize, callBack)
                    }
                }
            }
    }

    override fun initData() {
        mBinding.vm = mOrderManagerViewModel
    }
}