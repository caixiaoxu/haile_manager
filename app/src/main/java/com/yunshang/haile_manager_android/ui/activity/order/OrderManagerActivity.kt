package com.yunshang.haile_manager_android.ui.activity.order

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.OrderManagerViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.IntentParams.SearchSelectTypeParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.data.rule.DeviceIndicatorEntity
import com.yunshang.haile_manager_android.databinding.ActivityOrderManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceManagerErrorStatusBinding
import com.yunshang.haile_manager_android.databinding.ItemOrderListBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.view.IndicatorPagerTitleView
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import timber.log.Timber
import java.util.*

class OrderManagerActivity :
    BaseBusinessActivity<ActivityOrderManagerBinding, OrderManagerViewModel>(
        OrderManagerViewModel::class.java,
        BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    SearchSelectTypeParam.ShopResultCode -> {
                        intent.getStringExtra(SearchSelectTypeParam.ResultData)?.let { json ->
                            GsonUtils.json2List(json, SearchSelectParam::class.java)
                                ?.let { selected ->
                                    mViewModel.selectDepartments.value = selected
                                }
                        }
                    }

                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        mViewModel.selectDepartmentPositions.value =
                            IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                    }
                }
            }
        }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemOrderListBinding, OrderListEntity>(
            R.layout.item_order_list,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                if (UserPermissionUtils.hasOrderInfoPermission()) {
                    startActivity(
                        Intent(
                            this@OrderManagerActivity,
                            OrderDetailActivity::class.java
                        ).apply {
                            putExtras(IntentParams.OrderDetailParams.pack(orderNo = item.orderNo))
                        })
                }
            }
        }
    }

    private val dateDialog by lazy {
        DateSelectorDialog.Builder().apply {
            selectModel = 1
            maxDate = Calendar.getInstance().apply { time = Date() }
            onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                    Timber.i(
                        "----选择的开始日期${
                            DateTimeUtils.formatDateTime(
                                date1,
                                "yyyy-MM-dd"
                            )
                        }"
                    )
                    Timber.i(
                        "----选择的结束日期${
                            DateTimeUtils.formatDateTime(
                                date2,
                                "yyyy-MM-dd"
                            )
                        }"
                    )
                    //更换时间
                    mViewModel.startTime.value = date1
                    mViewModel.endTime.value = date2

                    mBinding.rvOrderManagerList.requestRefresh()
                }
            }
        }.build()
    }

    override fun layoutId(): Int = R.layout.activity_order_manager

    override fun backBtn(): View = mBinding.barOrderManagerTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.searchKey.value = IntentParams.SearchParams.parseKeyWord(intent)

        mViewModel.orderType = IntentParams.OrderManagerParams.parseOrderType(intent)
        mViewModel.deviceId = IntentParams.OrderManagerParams.parseDeviceId(intent)
        mViewModel.phone = IntentParams.OrderManagerParams.parsePhone(intent)

        if (0 != mViewModel.orderType) {
            mViewModel.startTime.value = DateTimeUtils.beforeDay(Date(), 6)
        }
    }

    override fun initEvent() {
        super.initEvent()

        // 刷新状态
        mViewModel.orderStatus.observe(this) { list ->
            mBinding.indicatorOrderStatus.navigator = CommonNavigator(this).apply {

                adapter = object : CommonNavigatorAdapter() {
                    override fun getCount(): Int = list.size

                    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                        return IndicatorPagerTitleView(context).apply {
                            normalColor = ContextCompat.getColor(
                                this@OrderManagerActivity,
                                R.color.color_black_65
                            )
                            selectedColor = ContextCompat.getColor(
                                this@OrderManagerActivity,
                                R.color.color_black_85
                            )
                            normalFontSize = 14f
                            selectFontSize = 14f
                            list[index].run {
                                text = title
                                setOnClickListener {
                                    mViewModel.curOrderStatus.value = value
                                    onPageSelected(index)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    override fun getIndicator(context: Context?): IPagerIndicator {
                        return LinePagerIndicator(context).apply {
                            mode = LinePagerIndicator.MODE_EXACTLY
                            lineWidth =
                                DimensionUtils.dip2px(this@OrderManagerActivity, 20f).toFloat()
                            lineHeight =
                                DimensionUtils.dip2px(this@OrderManagerActivity, 3f).toFloat()
                            roundRadius =
                                DimensionUtils.dip2px(this@OrderManagerActivity, 1.5f).toFloat()
                            setColors(
                                ContextCompat.getColor(
                                    this@OrderManagerActivity,
                                    R.color.color_black_85
                                )
                            )
                        }
                    }
                }
            }
        }

        // 切换工作状态
        mViewModel.curOrderStatus.observe(this) {
            mBinding.rvOrderManagerList.requestRefresh()
        }

        // 选择店铺
        mViewModel.selectDepartments.observe(this) {
            mBinding.tvOrderCategoryDepartment.text = when (val count: Int = (it?.size ?: 0)) {
                0 -> ""
                1 -> it?.firstOrNull()?.name ?: ""
                else -> "已选中${count}个门店"
            }
            mBinding.rvOrderManagerList.requestRefresh()
        }
        // 选择店铺点位
        mViewModel.selectDepartmentPositions.observe(this) {
            val list = it?.flatMap { item -> item.positionList ?: listOf() }
            mBinding.tvOrderCategoryDepartmentPosition.text =
                when (val count: Int = (list?.size ?: 0)) {
                    0 -> ""
                    1 -> list?.firstOrNull()?.name ?: ""
                    else -> "已选中${count}个营业点"
                }
            mBinding.rvOrderManagerList.requestRefresh()
        }
        // 列表刷新
        LiveDataBus.with(BusEvents.ORDER_LIST_STATUS, Int::class.java)?.observe(this) {
            if (mViewModel.curOrderStatus.value.isNullOrEmpty()) {
                mBinding.rvOrderManagerList.requestRefresh()
            } else {
                mAdapter.deleteItem { item -> item.id == it }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        if (1 == mViewModel.orderType) {
            val deviceName = IntentParams.OrderManagerParams.parseDeviceName(intent) ?: "设备订单"
            val content =
                "${deviceName}\nIMEI:${IntentParams.OrderManagerParams.parseDeviceImei(intent)}"
            mBinding.barOrderManagerTitle.setTitle(
                StringUtils.formatMultiStyleStr(
                    content,
                    arrayOf(
                        AbsoluteSizeSpan(DimensionUtils.sp2px(10f)),
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@OrderManagerActivity,
                                R.color.color_66171A1D
                            )
                        )
                    ), deviceName.length, content.length
                )
            )
        } else if (2 == mViewModel.orderType) {
            mBinding.barOrderManagerTitle.getTitle().movementMethod =
                LinkMovementMethod.getInstance()
            mBinding.barOrderManagerTitle.getTitle().highlightColor = Color.TRANSPARENT
            val phone = IntentParams.OrderManagerParams.parsePhone(intent)
            val content = "用户手机号\n${phone}"
            mBinding.barOrderManagerTitle.setTitle(
                StringUtils.formatMultiStyleStr(
                    content,
                    arrayOf(
                        AbsoluteSizeSpan(DimensionUtils.sp2px(10f)),
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@OrderManagerActivity,
                                R.color.colorPrimary
                            )
                        ),
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:${phone}")
                                startActivity(intent)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                //去掉下划线
                                ds.isUnderlineText = false
                            }
                        }
                    ), 5, content.length
                )
            )
        }

        if (mViewModel.searchKey.value.isNullOrEmpty()) {
            mBinding.barOrderManagerTitle.getRightBtn().run {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_search, 0)
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@OrderManagerActivity,
                            SearchActivity::class.java
                        ).apply {
                            putExtra(SearchType.SearchType, SearchType.Order)
                            putExtras(intent)
                        })
                }
            }
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
                    this@OrderManagerActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        SearchSelectTypeParam.pack(
                            SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = false,
                            moreSelect = true
                        )
                    )
                }
            )
        }

        // 点位
        mBinding.tvOrderCategoryDepartmentPosition.setOnClickListener {
            if (mViewModel.selectDepartments.value.isNullOrEmpty()) {
                SToast.showToast(this@OrderManagerActivity, "请先选择门店")
                return@setOnClickListener
            }
            startSearchSelect.launch(
                Intent(
                    this@OrderManagerActivity,
                    ShopPositionSelectorActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopPositionSelectorParams.pack(
                            mustSelect = false,
                            selectList = mViewModel.selectDepartmentPositions.value,
                            shopIdList = mViewModel.selectDepartments.value?.map { item -> item.id }
                                ?.toIntArray()
                        )
                    )
                }
            )
        }

        buildErrorStatus()

        // 刷新
        mBinding.tvOrderManagerListRefresh.setOnClickListener {
            mBinding.rvOrderManagerList.requestRefresh()
        }

        mBinding.rvOrderManagerList.layoutManager = LinearLayoutManager(this)
        mBinding.rvOrderManagerList.adapter = mAdapter

        mBinding.rvOrderManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<OrderListEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out OrderListEntity>?) -> Unit
                ) {
                    if (UserPermissionUtils.hasOrderListPermission()) {
                        mViewModel.requestOrderList(page, pageSize, callBack)
                    }
                }
            }
    }

    /**
     * 构建异常状态界面
     */
    private fun buildErrorStatus() {
        mBinding.llOrderErrorStatusList.buildChild<ItemDeviceManagerErrorStatusBinding, DeviceIndicatorEntity<Int>>(
            mViewModel.errorStatus
        ) { _, childBinding, data ->
            data.num.observe(this@OrderManagerActivity) {
                childBinding.tvDeviceManagerErrorStatus.text =
                    data.title + (if (it > 0) " $it" else " 0") + "单"
            }

            mViewModel.selectErrorStatus.observe(this) {
                childBinding.tvDeviceManagerErrorStatus.setTextColor(
                    ContextCompat.getColor(
                        this@OrderManagerActivity,
                        if (data.value == it) R.color.colorPrimary else R.color.color_black_85
                    )
                )
                childBinding.tvDeviceManagerErrorStatus.setBackgroundResource(if (data.value == it) R.drawable.shape_device_manager_error_status_selected_bg else R.drawable.shape_device_manager_error_status_bg)
            }

            childBinding.tvDeviceManagerErrorStatus.setOnClickListener {
                if (mViewModel.selectErrorStatus.value == data.value)
                    mViewModel.selectErrorStatus.value = null
                else
                    mViewModel.selectErrorStatus.value = data.value
                mBinding.rvOrderManagerList.requestRefresh()
            }
        }
    }

    override fun initData() {
    }
}