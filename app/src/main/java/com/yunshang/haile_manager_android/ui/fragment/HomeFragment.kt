package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StatusBarUtils
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
import com.yunshang.haile_manager_android.ui.activity.message.MessageCenterActivity
import com.yunshang.haile_manager_android.ui.activity.message.MessageListActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeActivity
import com.yunshang.haile_manager_android.ui.view.chart.BarChartRenderer
import com.yunshang.haile_manager_android.ui.view.chart.CustomMarkerView
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
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
                                        IncomeActivity::class.java
                                    ).apply {
                                        putExtra(IncomeActivity.ProfitType, 3)
                                        putExtra(IncomeActivity.SelectDay, it.date)
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

        LiveDataBus.with(BusEvents.MESSAGE_READ_STATUS)?.observe(this){
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
                startActivity(Intent(requireContext(), item.clz).apply {
                    item.bundle?.let { bundle ->
                        putExtras(bundle)
                    }
                })
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
            val dataSet = BarDataSet(positives, "收益趋势")
            dataSet.color = Color.parseColor("#FFEBD8")
            dataSet.setDrawValues(false)
            dataSet.highLightAlpha = 255
            dataSet.highLightColor = Color.parseColor("#F0A258") //选中颜色

            val dataSet1 = BarDataSet(negatives, "收益趋势")
            dataSet1.color = Color.parseColor("#C4F0E4")
            dataSet1.setDrawValues(false)
            dataSet1.highLightAlpha = 255
            dataSet1.highLightColor = Color.parseColor("#30C19A") //选中颜色

            val barData = BarData(dataSet, dataSet1)
            // 自定义marker
            val markerView = CustomMarkerView(context, R.layout.bar_chart_marker)
            markerView.chartView = mBinding.bcTrendChart
            markerView.addData(it)
            // 赋值
            mBinding.bcTrendChart.marker = markerView
            mBinding.bcTrendChart.data = barData
            mBinding.bcTrendChart.invalidate()
            mBinding.bcTrendChart.animateY(1500, Easing.EaseInOutQuad) // 启用Y轴方向的动画效果
            mBinding.bcTrendChart.animateXY(1500, 1500, Easing.EaseInOutQuad) // 启用XY轴方向的动画效果
            //选中当日
            val instance = Calendar.getInstance()
            val index = instance[Calendar.DAY_OF_MONTH]
            mBinding.bcTrendChart.highlightValue(index.toFloat(), 0, 0)
        }
    }

    override fun initData() {
        mViewModel.requestHomeData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.requestHomeData()
        }
    }
}