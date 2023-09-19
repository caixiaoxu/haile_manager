package com.yunshang.haile_manager_android.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.*
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.HomeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.HomeIncomeEntity
import com.yunshang.haile_manager_android.data.entities.MessageContentEntity
import com.yunshang.haile_manager_android.databinding.FragmentHomeBinding
import com.yunshang.haile_manager_android.databinding.IncludeHomeFuncItemBinding
import com.yunshang.haile_manager_android.databinding.IncludeHomeLastMsgItemBinding
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.activity.message.MessageCenterActivity
import com.yunshang.haile_manager_android.ui.activity.message.MessageListActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeCalendarActivity
import com.yunshang.haile_manager_android.ui.view.chart.BarChartRenderer
import com.yunshang.haile_manager_android.ui.view.chart.CustomMarkerView
import com.yunshang.haile_manager_android.ui.view.dialog.DeviceCategoryDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import timber.log.Timber
import java.lang.reflect.Field
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/7 13:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HomeFragment :
    BaseBusinessFragment<FragmentHomeBinding, HomeViewModel>(HomeViewModel::class.java, BR.vm) {

    // 权限
    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
            if (result.values.any { it }) {
                // 授权权限成功
                startQRActivity(false)
            } else {
                // 授权失败
                SToast.showToast(requireContext(), R.string.empty_permission)
            }
        }

    private fun startQRActivity(isOne: Boolean) {
        startQRCodeScan.launch(Intent(
            requireContext(),
            WeChatQRCodeScanActivity::class.java
        ).apply {
            putExtra("isOne", isOne)
        })
    }

    // 二维码
    private val startQRCodeScan =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                CameraScan.parseScanResult(result.data)?.let { origin ->
                    Timber.i("扫码:$origin")
                    StringUtils.getPayCode(origin)?.let { code ->
                        checkScanCode(code)
                    } ?: run {
                        (StringUtils.getPayImeiCode(origin) ?: run {
                            if (StringUtils.isImeiCode(origin)) origin else null
                        })?.let { imei ->
                            checkScanCode(imei = imei)
                        } ?: SToast.showToast(requireContext(), R.string.invalid_error)
                    }
                } ?: SToast.showToast(requireContext(), R.string.invalid_error)
            }
        }

    private fun checkScanCode(code: String? = null, imei: String? = null) {
        mViewModel.checkScanCode(code, imei) { deviceId ->
            if (UserPermissionUtils.hasDeviceInfoPermission()) {
                // 设备详情
                startActivity(
                    Intent(
                        requireContext(),
                        DeviceDetailActivity::class.java
                    ).apply {
                        putExtra(DeviceDetailActivity.GoodsId, deviceId)
                    }
                )
            }
        }
    }

    private val lastHighlight: Highlight? = null

    override fun layoutId(): Int = R.layout.fragment_home

    override fun initView() {
        mBinding.shared = mSharedViewModel
        //设置顶部距离
        val layoutParams = mBinding.bgHomeTitle.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = StatusBarUtils.getStatusBarHeight()
        mBinding.bgHomeTitle.layoutParams = layoutParams

        mBinding.ibHomeMsg.setOnClickListener {
            startActivity(Intent(requireContext(), MessageCenterActivity::class.java))
        }

        mBinding.ibHomeScan.setOnClickListener {
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

//        mBinding.ibHomeIncomeChange.setOnClickListener {
//            mViewModel.profitIncomeType.value = if (1 == mViewModel.profitIncomeType.value) 2 else 1
//            mViewModel.requestHomeIncome()
//        }

        initBarChart()

        mBinding.tvTrendDate.setOnClickListener {
            val dailog = DateSelectorDialog.Builder().apply {
                showModel = 1
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        Timber.i("选择的日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM")}")
                        mViewModel.selectedDate.value = date1
                    }
                }
            }.build()
            dailog.show(childFragmentManager)
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

    /**
     * 初始化图表界面
     */
    private fun initBarChart() {
        // 通过反射自定义柱子的形状
        try {
            val mAnimatorField: Field =
                mBinding.bcTrendChart.javaClass.superclass.superclass.getDeclaredField("mAnimator")
            mAnimatorField.isAccessible = true
            val mAnimator = mAnimatorField[mBinding.bcTrendChart] as ChartAnimator
            val mViewPortHandlerField: Field = mBinding.bcTrendChart.javaClass.superclass.superclass
                .getDeclaredField("mViewPortHandler")
            mViewPortHandlerField.isAccessible = true
            val mViewPortHandler = mViewPortHandlerField[mBinding.bcTrendChart] as ViewPortHandler
            val mRendererField: Field =
                mBinding.bcTrendChart.javaClass.superclass.superclass.getDeclaredField("mRenderer")
            mRendererField.isAccessible = true
            mRendererField[mBinding.bcTrendChart] =
                BarChartRenderer(
                    mBinding.bcTrendChart,
                    mAnimator,
                    mViewPortHandler
                )
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

        mBinding.bcTrendChart.setTouchEnabled(true) // 设置是否可以触摸
        mBinding.bcTrendChart.isDragEnabled = false // 是否可以拖拽
        mBinding.bcTrendChart.setScaleEnabled(false) // 是否可以缩放
        mBinding.bcTrendChart.setPinchZoom(false) //y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
        mBinding.bcTrendChart.description.isEnabled = false // 隐藏图表描述
        mBinding.bcTrendChart.setDrawGridBackground(false) // 不绘制背景
        mBinding.bcTrendChart.setDrawBarShadow(false) // 不绘制阴影
        mBinding.bcTrendChart.setDrawBorders(false) // 不绘制边框
        mBinding.bcTrendChart.setDrawValueAboveBar(false) //不显示值
        mBinding.bcTrendChart.isHighlightPerTapEnabled = false // 配合下面的点击事件使用，取消高亮

        mBinding.bcTrendChart.legend.isEnabled = false
        // 不显示x轴
        mBinding.bcTrendChart.xAxis.run {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            labelCount = 31
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    axis.textColor = Color.parseColor("#999999")
                    axis.textSize = 10f
                    val v = value.toInt()
                    return if (1 == v || 0 == v % 5) v.toString() + "" else "."
                }
            }
        }
        // 不显示右Y轴
        mBinding.bcTrendChart.axisRight.isEnabled = false
        //左Y轴
        mBinding.bcTrendChart.axisLeft.run {
            //设置是否开启绘制轴的标签
            setDrawLabels(false)
            //是否绘制轴线
            setDrawAxisLine(false)
            // 数量
            setLabelCount(4, false)
            spaceTop = 52f
            axisMinimum = 0f
            //是否绘制0所在的网格线/默认false绘制
            zeroLineColor = Color.parseColor("#F0A258")
            setDrawZeroLine(true)
            // 绘制虚线网络
            gridColor = Color.parseColor("#F0A258")
            enableGridDashedLine(8f, 8f, 34f)
        }

        mBinding.bcTrendChart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(
                motionEvent: MotionEvent,
                chartGesture: ChartGesture
            ) {
            }

            override fun onChartGestureEnd(motionEvent: MotionEvent, chartGesture: ChartGesture) {}
            override fun onChartLongPressed(motionEvent: MotionEvent) {}
            override fun onChartDoubleTapped(motionEvent: MotionEvent) {}
            override fun onChartSingleTapped(e: MotionEvent) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val h: Highlight? =
                    mBinding.bcTrendChart.getHighlightByTouchPoint(x.toFloat(), y.toFloat())
                if (h == null || h.equalTo(lastHighlight)) {
                    return
                }
                if (mBinding.bcTrendChart.valuesToHighlight()) {
                    val highlighted: Array<Highlight> = mBinding.bcTrendChart.highlighted
                    val marker: CustomMarkerView = mBinding.bcTrendChart.marker as CustomMarkerView
                    val rect = Rect()
                    marker.getDrawingRect(rect)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val offset: MPPointF = marker.getOffsetForDrawingAtPoint(
                            highlighted[0].drawX,
                            highlighted[0].drawY
                        )
                        rect.offset((highlighted[0].drawX + offset.x).toInt(), 0)
                        val contains = rect.contains(x, y)
                        if (contains && e.action == MotionEvent.ACTION_UP) {
                            // 跳转到收详情

                            marker.curBean?.let {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        IncomeCalendarActivity::class.java
                                    ).apply {
                                        putExtra(IncomeCalendarActivity.ProfitType, 4)
                                        putExtra(IncomeCalendarActivity.SelectDay, it.date)
                                        putExtra(
                                            IncomeCalendarActivity.ProfitIncomeType,
                                            mViewModel.profitIncomeType.value
                                        )
                                    })
                            }
                            return
                        }
                    }
                }
                mBinding.bcTrendChart.highlightValue(h, true)
            }

            override fun onChartFling(
                motionEvent: MotionEvent,
                motionEvent1: MotionEvent,
                v: Float,
                v1: Float
            ) {
            }

            override fun onChartScale(motionEvent: MotionEvent, v: Float, v1: Float) {}
            override fun onChartTranslate(motionEvent: MotionEvent, v: Float, v1: Float) {}
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasUserPermission.observe(this) {
            initProfitIncomeType()
            mViewModel.requestHomeIncome()
        }

        // 消息列表 
        mViewModel.lastMsgList.observe(this) { list ->
            list?.let {
                mBinding.tvLastMsgNum.setOnClickListener {
                    startActivity(Intent(requireContext(), MessageCenterActivity::class.java))
                }
                mBinding.viewLastMsgUnread.setOnClickListener {
                    startActivity(Intent(requireContext(), MessageCenterActivity::class.java))
                }

                mBinding.llLastMsgList.removeAllViews()
                val lastMsg = it.take(2)
                val layoutInflater = LayoutInflater.from(context)
                lastMsg.forEach { msg ->
                    val mMsgItemBinding = DataBindingUtil.inflate<IncludeHomeLastMsgItemBinding>(
                        layoutInflater,
                        R.layout.include_home_last_msg_item,
                        null, false
                    )
                    mMsgItemBinding.lifecycleOwner = this
                    mMsgItemBinding.root.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        DimensionUtils.dip2px(requireContext(), 30f)
                    )
                    mMsgItemBinding.message = msg

                    val messageContentEntity =
                        GsonUtils.json2Class(msg.content, MessageContentEntity::class.java)
                    mMsgItemBinding.tvLastMsgContent.text =
                        messageContentEntity?.shortDescription ?: ""
                    mMsgItemBinding.tvLastMsgTime.text = DateTimeUtils.getFriendlyTime(
                        DateTimeUtils.formatDateFromString(msg.createTime),
                        false
                    )
                    mMsgItemBinding.root.setOnClickListener {
                        startActivity(
                            Intent(
                                requireContext(),
                                MessageListActivity::class.java
                            ).apply {
                                putExtras(
                                    IntentParams.MessageListParams.pack(
                                        msg.typeId,
                                        msg.title
                                    )
                                )
                            })
                    }
                    mBinding.llLastMsgList.addView(mMsgItemBinding.root)
                }
            }
        }

        // 收益趋势
        mViewModel.homeIncomeList.observe(this) {
            refreshChartData(it)
        }

        // 选择时间
        mViewModel.selectedDate.observe(this) {
            mViewModel.requestHomeIncome()
        }

        // 功能管理
        mViewModel.funcList.observe(this) { list ->
            refreshFuncArea(list, mBinding.llFuncArea, mBinding.glFuncArea)
        }
        // 营销中心
        mViewModel.marketingList.observe(this) { list ->
            refreshFuncArea(list, mBinding.llMarketingArea, mBinding.glMarketingArea)
        }
        // 资金管理
        mViewModel.capitalList.observe(this) { list ->
            refreshFuncArea(list, mBinding.llCapitalArea, mBinding.glCapitalArea)
        }

        // 设备权限
        mSharedViewModel.hasDevicePermission.observe(this) {
            mViewModel.funcList.value?.let { list ->
                mViewModel.funcList.value = list.apply { this[0].isShow = it }
            }
        }
        // 门店权限
        mSharedViewModel.hasShopPermission.observe(this) {
            mViewModel.funcList.value?.let { list ->
                mViewModel.funcList.value = list.apply { this[1].isShow = it }
            }
        }
        // 订单权限
        mSharedViewModel.hasOrderPermission.observe(this) {
            mViewModel.funcList.value?.let { list ->
                mViewModel.funcList.value = list.apply { this[2].isShow = it }
            }
        }
        // 人员权限
        mSharedViewModel.hasPersonPermission.observe(this) {
            mViewModel.funcList.value?.let { list ->
                mViewModel.funcList.value = list.apply { this[3].isShow = it }
            }
        }
        // 优惠权限
        mSharedViewModel.hasMarketingPermission.observe(this) {
            mViewModel.marketingList.value?.let { list ->
                mViewModel.marketingList.value = list.apply { this[0].isShow = it }
            }
        }
        // 分账权限
        mSharedViewModel.hasDistributionPermission.observe(this) {
            mViewModel.capitalList.value?.let { list ->
                mViewModel.capitalList.value = list.apply { this[0].isShow = it }
            }
        }

        LiveDataBus.with(BusEvents.MESSAGE_READ_STATUS)?.observe(this) {
            mViewModel.requestMsgData()
        }
    }

    /**
     * 刷新功能区域界面
     */
    private fun refreshFuncArea(
        list: List<HomeViewModel.FunItem>,
        llArea: LinearLayout,
        glArea: GridLayout
    ) {
        // 根据权限过滤
        val filterList = list.filter { it.isShow }
        // 如果都没有就不显示
        llArea.visibility = if (filterList.isEmpty()) View.GONE else View.VISIBLE

        val layoutInflater = LayoutInflater.from(context)
        // 清除子View
        glArea.removeAllViews()
        // 计算宽度，(屏幕宽-边距)/列数
        val funcW =
            (ScreenUtils.screenWidth - 2 * DimensionUtils.dip2px(
                requireContext(),
                12f
            )) / glArea.columnCount

        // 边距
        val marginT = DimensionUtils.dip2px(requireContext(), 12f)
        filterList.forEach { item ->
            // 加载子view
            val mFuncAreaBinding = DataBindingUtil.inflate<IncludeHomeFuncItemBinding>(
                layoutInflater,
                R.layout.include_home_func_item,
                null, false
            )
            mFuncAreaBinding.lifecycleOwner = this
            mFuncAreaBinding.root.layoutParams = LinearLayout.LayoutParams(
                funcW,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = marginT
            }
            // 点击事件
            mFuncAreaBinding.root.setOnClickListener {
                if (item.icon == R.mipmap.icon_device_manager) {
                    DeviceCategoryDialog.Builder().apply {
                        onDeviceCodeSelectListener = { type ->
                            startActivity(Intent(requireContext(), item.clz).apply {
                                putExtras(IntentParams.DeviceManagerParams.pack(categoryBigType = type))
                            })
                        }
                    }.build().show(childFragmentManager)
                } else {
                    startActivity(Intent(requireContext(), item.clz).apply {
                        item.bundle?.let { bundle ->
                            putExtras(bundle)
                        }
                    })
                }
            }
            // 数据
            mFuncAreaBinding.funcItem = item
            glArea.addView(mFuncAreaBinding.root)
        }
    }

    /**
     * 刷新图表数据
     */
    private fun refreshChartData(homeIncomeEntities: List<HomeIncomeEntity>?) {
        homeIncomeEntities?.let {
            //拆分正负数据
            val positives = ArrayList<BarEntry>()
            val negatives = ArrayList<BarEntry>()
            it.forEachIndexed { index, income ->
                try {
                    val amount: Float = income.amount.toFloat()
                    if (amount < 0) {
                        negatives.add(BarEntry((index + 1).toFloat(), -amount))
                    } else {
                        positives.add(BarEntry((index + 1).toFloat(), amount))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 如果正负收益列表都为空，或者没有权限，不显示
            if ((positives.isEmpty() && negatives.isEmpty()) || !UserPermissionUtils.hasProfitPermission()) {
                mBinding.clHomeTrend.visibility = View.GONE
            } else {
                mBinding.clHomeTrend.visibility = View.VISIBLE
                val dataList = arrayListOf<IBarDataSet>()
                if (positives.isNotEmpty()) {
                    val dataSet = BarDataSet(positives, "收益趋势")
                    dataSet.color = Color.parseColor("#FFEBD8")
                    dataSet.setDrawValues(false)
                    dataSet.highLightAlpha = 255
                    dataSet.highLightColor = Color.parseColor("#F0A258") //选中颜色
                    dataList.add(dataSet)
                }
                if (negatives.isNotEmpty()) {
                    val dataSet1 = BarDataSet(negatives, "收益趋势")
                    dataSet1.color = Color.parseColor("#C4F0E4")
                    dataSet1.setDrawValues(false)
                    dataSet1.highLightAlpha = 255
                    dataSet1.highLightColor = Color.parseColor("#30C19A") //选中颜色
                    dataList.add(dataSet1)
                }

                val barData = BarData(dataList)
                // 自定义marker
                val markerView = CustomMarkerView(context, R.layout.bar_chart_marker)
                markerView.chartView = mBinding.bcTrendChart
                markerView.addData(it)
                // 赋值
                mBinding.bcTrendChart.marker = markerView
                mBinding.bcTrendChart.data = barData
                mBinding.bcTrendChart.postInvalidateDelayed(1000)
//                mBinding.bcTrendChart.animateXY(1000, 1000, Easing.EaseInOutQuad) // 启用XY轴方向的动画效果
                //选中当日
                val instance = Calendar.getInstance()
                val index = instance[Calendar.DAY_OF_MONTH]
                mBinding.bcTrendChart.highlightValue(index.toFloat(), 0, 0)
            }
        }
    }

    override fun initData() {
        initProfitIncomeType()
        mViewModel.requestHomeData()
    }

    private fun initProfitIncomeType() {
        if (UserPermissionUtils.hasProfitPermission()) {
            if (UserPermissionUtils.hasProfitMerchantPermission()) {
                mViewModel.profitIncomeType.value = 2
            } else if (UserPermissionUtils.hasProfitHomePermission()) {
                mViewModel.profitIncomeType.value = 1
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.requestHomeData()
            if (mViewModel.homeIncomeList.value.isNullOrEmpty()) {
                mViewModel.requestHomeIncome()
            }
        }
    }
}