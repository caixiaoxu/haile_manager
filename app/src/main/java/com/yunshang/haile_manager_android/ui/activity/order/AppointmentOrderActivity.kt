package com.yunshang.haile_manager_android.ui.activity.order

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.AppointmentOrderViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.databinding.ActivityAppointmentOrderBinding
import com.yunshang.haile_manager_android.databinding.ItemAppointmentOrderListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CancelContentDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.NumberUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber
import java.util.*

class AppointmentOrderActivity :
    BaseBusinessActivity<ActivityAppointmentOrderBinding, AppointmentOrderViewModel>(
        AppointmentOrderViewModel::class.java, BR.vm
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

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemAppointmentOrderListBinding, OrderListEntity>(
            R.layout.item_appointment_order_list,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.llOrderListSpecs?.removeAllViews()
            item.skuList.forEach { sku ->
                mItemBinding?.llOrderListSpecs?.addView(
                    TextView(this@AppointmentOrderActivity).apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this@AppointmentOrderActivity,
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
            mItemBinding?.tvOrderListCancel?.setOnClickListener {
                CancelContentDialog.Builder(
                    StringUtils.getString(R.string.cancel_order),
                    StringUtils.getString(R.string.cancel_order_hint)
                ).apply {
                    positiveClickListener = {
                        mViewModel.cancelAppointmentOrder(item.orderNo, it) {
                            mBinding.rvAppointmentOrderList.requestRefresh()
                        }
                    }
                }.build().show(supportFragmentManager)
            }
            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@AppointmentOrderActivity,
                        OrderDetailActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.OrderDetailParams.pack(
                                orderNo = item.orderNo,
                                isAppoint = true
                            )
                        )
                    })
            }
        }
    }

    private val dateDialog by lazy {
        DateSelectorDialog.Builder().apply {
            selectModel = 1
            maxDate = Calendar.getInstance().apply { time = Date() }
            onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                    Timber.i("----选择的开始日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")}")
                    Timber.i("----选择的结束日期${DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd")}")
                    //更换时间
                    mViewModel.startTime.value = date1
                    mViewModel.endTime.value = date2

                    mBinding.rvAppointmentOrderList.requestRefresh()
                }
            }
        }.build()
    }


    override fun initIntent() {
        super.initIntent()
        mViewModel.searchKey.value = IntentParams.SearchParams.parseKeyWord(intent)
    }

    override fun layoutId(): Int = R.layout.activity_appointment_order

    override fun backBtn(): View = mBinding.barAppointmentOrderTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        // 刷新状态
        mViewModel.appointOrderStatus.observe(this) { list ->
            mBinding.indicatorOrderStatus.navigator = CommonNavigator(this).apply {

                adapter = object : CommonNavigatorAdapter() {
                    override fun getCount(): Int = list.size

                    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                        return SimplePagerTitleView(context).apply {
                            normalColor = Color.parseColor("#666666")
                            selectedColor = Color.WHITE
                            list[index].run {
                                text = title
                                setOnClickListener {
                                    mViewModel.curAppointmentOrderStatus.value = value
                                    onPageSelected(index)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    override fun getIndicator(context: Context?): IPagerIndicator {
                        return WrapPagerIndicator(context).apply {
                            verticalPadding =
                                DimensionUtils.dip2px(this@AppointmentOrderActivity, 4f)
                            fillColor = ContextCompat.getColor(
                                this@AppointmentOrderActivity,
                                R.color.colorPrimary
                            )
                            roundRadius =
                                DimensionUtils.dip2px(this@AppointmentOrderActivity, 14f).toFloat()
                        }
                    }
                }
            }
        }

        // 切换工作状态
        mViewModel.curAppointmentOrderStatus.observe(this) {
            mBinding.rvAppointmentOrderList.requestRefresh()
        }

        // 选择店铺
        mViewModel.selectDepartment.observe(this) {
            mBinding.tvOrderCategoryDepartment.text = it.name
            mBinding.rvAppointmentOrderList.requestRefresh()
        }

        // 列表刷新
        LiveDataBus.with(BusEvents.ORDER_LIST_STATUS, Int::class.java)?.observe(this) {
            if (mViewModel.curAppointmentOrderStatus.value.isNullOrEmpty()) {
                mBinding.rvAppointmentOrderList.requestRefresh()
            } else {
                mAdapter.deleteItem { item -> item.id == it }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.viewAppointmentOrderSearchBg.setOnClickListener {
            startActivity(Intent(this@AppointmentOrderActivity, SearchActivity::class.java).apply {
                putExtra(SearchType.SearchType, SearchType.AppointOrder)
            })
        }
        mBinding.tvOrderCategoryTime.setOnClickListener {
            dateDialog.show(
                supportFragmentManager,
                mViewModel.startTime.value,
                mViewModel.endTime.value
            )
        }

        // 所属门店
        mBinding.tvOrderCategoryDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@AppointmentOrderActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = false
                        )
                    )
                }
            )
        }

        // 刷新
        mBinding.tvAppointmentOrderListRefresh.setOnClickListener {
            mBinding.rvAppointmentOrderList.requestRefresh()
        }

        mBinding.rvAppointmentOrderList.layoutManager = LinearLayoutManager(this)
        mBinding.rvAppointmentOrderList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvAppointmentOrderList.adapter = mAdapter

        mBinding.rvAppointmentOrderList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<OrderListEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out OrderListEntity>?) -> Unit
                ) {
                    mViewModel.requestAppointmentOrderList(page, pageSize, callBack)
                }
            }
    }

    override fun initData() {
    }
}