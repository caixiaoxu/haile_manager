package com.shangyun.haile_manager_android.ui.view.dialog.dateTime

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.contrarywind.listener.OnItemSelectedListener
import com.contrarywind.view.WheelView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.DialogDateSelectorBinding
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import java.util.*
import kotlin.math.min


/**
 * Title : 日期选择Dialog
 * Author: Lsy
 * Date: 2023/4/11 09:47
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DateSelectorDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val DATE_TIME_TAG = "date_time_tag"
    private lateinit var mBinding: DialogDateSelectorBinding

    // 选择的日期类型，0开始，1结束
    private var selectType: Int = 0

    // 记录日期
    private val startCal = Calendar.getInstance().apply { time = Date() }
    private var endCal: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DateTimeStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            if (this is BottomSheetDialog) {
                setOnShowListener {
                    behavior.isHideable = false
                    // dialog上还有一层viewGroup，需要设置成透明
                    (requireView().parent as ViewGroup).setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogDateSelectorBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 关闭
        mBinding.ibDateTimeClose.setOnClickListener {
            dismiss()
        }

        // 确定
        mBinding.tvDateTimeSure.setOnClickListener {
            if (1 == builder.selectModel) {
                if (null == endCal) {
                    SToast.showToast(msg = "请选择结束日期")
                    return@setOnClickListener
                }
                if (endCal!!.before(startCal)) {
                    SToast.showToast(msg = "结束日期不能早于开始日期")
                    return@setOnClickListener
                }
            }
            builder.onDateSelectedListener?.onDateSelect(
                builder.selectModel,
                startCal.time,
                endCal?.time
            )
            dismiss()
        }

        //单选显示，多选不显示
        mBinding.clDateSelectorMultiple.visibility =
            if (0 != builder.selectModel) View.VISIBLE else View.GONE

        // 多选
        if (0 != builder.selectModel) {
            initTimeValView()
            refreshStartTimeVal()
            mBinding.tvDateTimeStart.setOnClickListener {
                if (0 != selectType) {
                    selectType = 0
                    initTimeValView()
                    refreshAllWheelData()
                }
            }
            refreshEndTimeVal()
            mBinding.tvDateTimeEnd.setOnClickListener {
                if (1 != selectType) {
                    selectType = 1
                    initTimeValView()
                    refreshAllWheelData()
                }
            }
        }

        // 初始化滚轮
        initWheelView(
            mBinding.wvDateTimeYear,
            (0 == builder.showModel || 1 == builder.showModel)
        ) { index ->
            getCurSelectCalender().set(Calendar.YEAR, builder.minDate.get(Calendar.YEAR) + index)

            if (0 != builder.selectModel) {
                refreshTimeVal()
            }
        }
        initWheelView(mBinding.wvDateTimeMonth, true) { index ->
            getCurSelectCalender().set(Calendar.MONTH, index)
            refreshDayData()
            if (0 != builder.selectModel) {
                refreshTimeVal()
            }
        }
        initWheelView(
            mBinding.wvDateTimeDay,
            (0 == builder.showModel || 2 == builder.showModel)
        ) { index ->
            getCurSelectCalender().set(Calendar.DAY_OF_MONTH, 1 + index)
            if (0 != builder.selectModel) {
                refreshTimeVal()
            }
        }

        // 加载所有滚轮数据
        refreshAllWheelData()
    }

    /**
     * 刷新时间值
     */
    private fun refreshTimeVal() {
        if (0 == selectType) {
            refreshStartTimeVal()
        } else {
            refreshEndTimeVal()
        }
    }

    /**
     * 设置显示时间控件参数
     */
    private fun initTimeValView() {
        if (0 == selectType) {
            mBinding.tvDateTimeStart.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )
            mBinding.tvDateTimeStart.setBackgroundResource(R.drawable.shape_bottom_stroke_half_f0a258)
        } else {
            mBinding.tvDateTimeStart.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.common_txt_hint_color,
                    null
                )
            )
            mBinding.tvDateTimeStart.setBackgroundResource(R.drawable.shape_bottom_stroke_half_cccccc)
        }

        if (1 == selectType) {
            mBinding.tvDateTimeEnd.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )
            mBinding.tvDateTimeEnd.setBackgroundResource(R.drawable.shape_bottom_stroke_half_f0a258)
        } else {
            mBinding.tvDateTimeEnd.setTextColor(
                ResourcesCompat.getColor(resources, R.color.common_txt_hint_color, null)
            )
            mBinding.tvDateTimeEnd.setBackgroundResource(R.drawable.shape_bottom_stroke_half_cccccc)
        }
    }

    private fun getFormatStr(): String = when (builder.showModel) {
        0 -> "yyyy-MM-dd"
        1 -> "yyyy-MM"
        2 -> "MM-dd"
        else -> "yyyy-MM-dd"
    }

    /**
     * 刷新开始时间
     */
    private fun refreshStartTimeVal() {
        mBinding.tvDateTimeStart.text =
            DateTimeUtils.formatDateTime(getFormatStr(), startCal.time)
    }

    /**
     * 刷新结束时间
     */
    private fun refreshEndTimeVal() {
        mBinding.tvDateTimeEnd.text = endCal?.time?.let {
            DateTimeUtils.formatDateTime(getFormatStr(), it)
        } ?: "结束时间"
    }

    /**
     * 获取当前选择的日期
     */
    private fun getCurSelectCalender(): Calendar {
        return if (0 == selectType) startCal else {
            if (null == endCal) {
                endCal = Calendar.getInstance()
            }
            endCal!!
        }
    }


    /**
     * 初始化滚轮
     */
    private fun initWheelView(
        wheelView: WheelView,
        isShow: Boolean,
        onItemSelected: OnItemSelectedListener
    ) {
        if (isShow) {
            wheelView.visibility = View.VISIBLE
            wheelView.setCyclic(false)//不循环
            wheelView.setTextSize(22f)//字体
            wheelView.setItemsVisibleCount(7)//可显示7个
            wheelView.setDividerColor(Color.TRANSPARENT)
            wheelView.setBackgroundColor(Color.TRANSPARENT)
            wheelView.setTextColorCenter(Color.parseColor("#333333"))//中心颜色
            wheelView.setTextColorOut(Color.parseColor("#999999"))
            wheelView.setOnItemSelectedListener(onItemSelected)
        } else {
            wheelView.visibility = View.GONE
        }
    }

    /**
     * 刷新所有滚轮数据
     */
    private fun refreshAllWheelData() {
        if ((0 == builder.showModel || 1 == builder.showModel)) {
            refreshWheelData(
                mBinding.wvDateTimeYear,
                getCurSelectCalender().get(Calendar.YEAR) - builder.minDate.get(Calendar.YEAR),
                DateTimeUtils.getYearSection(
                    builder.minDate.get(Calendar.YEAR),
                    builder.maxDate.get(Calendar.YEAR)
                )
            )
        }
        refreshWheelData(
            mBinding.wvDateTimeMonth,
            getCurSelectCalender().get(Calendar.MONTH),
            DateTimeUtils.getMonthSection()
        )
        refreshDayData()
    }

    /**
     * 刷新天份数据
     */
    private fun refreshDayData() {
        if ((0 == builder.showModel || 2 == builder.showModel)) {
            refreshWheelData(
                mBinding.wvDateTimeDay,
                min(
                    getCurSelectCalender().get(Calendar.DAY_OF_MONTH),
                    getCurSelectCalender().getActualMaximum(Calendar.DAY_OF_MONTH)
                ) - 1,
                DateTimeUtils.getDaySection(getCurSelectCalender().getActualMaximum(Calendar.DAY_OF_MONTH)),
            )
        }
    }

    /**
     * 刷新滚轮数据
     */
    private fun refreshWheelData(wheelView: WheelView, curIndex: Int, list: List<String>) {
        wheelView.currentItem = curIndex // 默认选中
        wheelView.adapter = ArrayWheelAdapter(list)
        wheelView.invalidate()
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = builder.isCancelable
        show(manager, DATE_TIME_TAG)
    }

    internal class Builder {
        // 0单选/1区间
        var selectModel: Int = 0

        // 显示年月日，0年月日/1年月/2月是
        var showModel: Int = 0

        // 最小或最大时间
        var minDate: Calendar = Calendar.getInstance().apply { set(1920, 1, 1) }
        var maxDate: Calendar = Calendar.getInstance().apply { set(2099, 12, 31) }

        // 不可取消
        var isCancelable = true

        // 选择监听
        var onDateSelectedListener: OnDateSelectListener? = null

        /**
         * 构建
         */
        fun build(): DateSelectorDialog = DateSelectorDialog(this)
    }

    interface OnDateSelectListener {
        /**
         * 时间回调，
         * @param mode 0单选/1区间
         * @param date1 必有
         * @param date2 单选为null
         */
        fun onDateSelect(mode: Int, date1: Date, date2: Date?)
    }
}