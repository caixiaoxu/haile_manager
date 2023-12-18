package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeCalendarViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.ProfitStatisticsEntity
import com.yunshang.haile_manager_android.data.rule.ICalendarEntity
import com.yunshang.haile_manager_android.databinding.ActivityIncomeCalendarBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeCalendarBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeListByDayBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import com.yunshang.haile_manager_android.utils.ViewUtils
import timber.log.Timber
import java.util.*
import kotlin.math.min

class IncomeCalendarActivity :
    BaseBusinessActivity<ActivityIncomeCalendarBinding, IncomeCalendarViewModel>(
        IncomeCalendarViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val ProfitType = "profitType"
        const val ProfitSearchId = "profitSearchId"
        const val DeviceName = "deviceName"
        const val SelectDay = "selectDay"
        const val ProfitIncomeType = "profitIncomeType"
    }

    private val mIncomeAdapter: CommonRecyclerAdapter<ItemIncomeCalendarBinding, ICalendarEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_calendar,
            BR.item
        ) { mItemBinding, _, item ->
            val isAfterToday = item.afterToday()
            mItemBinding?.root?.setOnClickListener {
                if (-1 == item.type) return@setOnClickListener
                if (isAfterToday) return@setOnClickListener
                mViewModel.selectDay.value = item.getDate()
            }
            mViewModel.selectDay.observe(this) { day ->
                val isSelect = item.isSelect(day)
                mItemBinding?.root?.setBackgroundColor(
                    if (isSelect) item.curTypeBGColor else ContextCompat.getColor(
                        this@IncomeCalendarActivity,
                        R.color.white
                    )
                )
                mItemBinding?.tvIncomeCalendarDayNum?.setTextColor(
                    ContextCompat.getColor(
                        this@IncomeCalendarActivity,
                        if (isSelect) R.color.white else R.color.common_txt_color
                    )
                )
                mItemBinding?.tvIncomeCalendarDayAmount?.setTextColor(
                    if (isSelect) Color.WHITE else if (isAfterToday) ContextCompat.getColor(
                        Constants.APP_CONTEXT,
                        R.color.common_txt_hint_color
                    ) else item.curTypeColor
                )
            }
        }
    }
    private val mIncomeListAdapter: CommonRecyclerAdapter<ItemIncomeListByDayBinding, ProfitStatisticsEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_list_by_day,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                if (UserPermissionUtils.hasOrderInfoPermission()) {
                    startActivity(
                        if ("1000" == item.categoryCode) {
                            Intent(
                                this@IncomeCalendarActivity,
                                IncomeDetailActivity::class.java
                            ).apply {
                                putExtra(IncomeDetailActivity.DetailType, 2)
                                putExtra(IncomeDetailActivity.OrderNo, item.orderNo)
                            }
                        } else {
                            Intent(
                                this@IncomeCalendarActivity,
                                OrderDetailActivity::class.java
                            ).apply {
                                putExtras(IntentParams.OrderDetailParams.pack(item.orderId))
                            }
                        }
                    )
                } else {
                    SToast.showToast(this@IncomeCalendarActivity, R.string.no_permission)
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_income_calendar

    override fun backBtn(): View = mBinding.barIncomeTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.profitType = intent.getIntExtra(ProfitType, 3)
        mViewModel.profitSearchId = intent.getIntExtra(ProfitSearchId, -1)
        mViewModel.deviceName =
            if (1 == mViewModel.profitType) "门店" else intent.getStringExtra(DeviceName) ?: ""
//        mViewModel.profitIncomeType = intent.getIntExtra(ProfitIncomeType, 1)

        DateTimeUtils.formatDateFromString(intent.getStringExtra(SelectDay))?.let {
            mViewModel.selectDay.postValue(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.selectDay.observe(this) {
            mBinding.tvIncomeDateTitle.text = StringUtils.getString(
                R.string.total_income_for_day,
                DateTimeUtils.formatDateTime(it, "yyyy年MM月dd日")
            )
            mViewModel.requestTotalAmount(false)
            mBinding.rvIncomeListForDate.requestRefresh()
        }
        mViewModel.selectMonth.observe(this) {
            mViewModel.requestTotalAmount(true)
            mViewModel.requestIncomeByDate()

            mViewModel.selectDay.value?.let { curDay ->
                val c1 = Calendar.getInstance().apply {
                    time = it
                }

                val c2 = Calendar.getInstance().apply {
                    time = curDay
                }

                val max = c1.getActualMaximum(Calendar.DAY_OF_MONTH)
                val curD = c2.get(Calendar.DAY_OF_MONTH)
                c1.set(Calendar.DAY_OF_MONTH, min(max, curD))
                mViewModel.selectDay.value = c1.time
            }
        }
        mViewModel.calendarIncome.observe(this) {
            mIncomeAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barIncomeTitle.setTitle(
            when (mViewModel.profitType) {
                1 -> R.string.shop_income_calendar
                2 -> R.string.device_income_calendar
                else -> R.string.income_calendar
            }
        )

        mBinding.tvIncomeSelectDate.setOnClickListener {
            DateSelectorDialog.Builder().apply {
                showModel = 1
                maxDate = Calendar.getInstance().apply { time = Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        Timber.i("----选择的日期${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")}")
                        //更换时间
                        mViewModel.selectMonth.value = date1
                    }
                }
            }.build().show(supportFragmentManager)
        }

        val arr = resources.getStringArray(R.array.week_arr)
        ViewUtils.refreshLinearLayoutChild(
            mBinding.llIncomeCalendarTitle,
            arr,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                weight = 1f
            }) { str ->
            AppCompatTextView(this).apply {
                gravity = Gravity.CENTER
                textSize = 10f
                text = str
                setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.common_sub_txt_color,
                        null
                    )
                )
            }
        }

        mBinding.rvIncomeCalendar.layoutManager = GridLayoutManager(this, arr.size)
        mBinding.rvIncomeCalendar.adapter = mIncomeAdapter

        // 收支切换
        mBinding.tvIncomeListForDateOfType.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                "", listOf(
                    SearchSelectParam(0, getString(R.string.all)),
                    SearchSelectParam(1, getString(R.string.earning)),
                    SearchSelectParam(2, getString(R.string.expend)),
                )
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.transactionType = if (0 == data.id) null else data.id
                            mBinding.tvIncomeListForDateOfType.text = data.name
                            mBinding.rvIncomeListForDate.requestRefresh()
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 收支列表
        mBinding.rvIncomeListForDate.layoutManager = LinearLayoutManager(this)
        mBinding.rvIncomeListForDate.enableRefresh = false
        ResourcesCompat.getDrawable(resources, R.drawable.divder_efefef, null)?.let {
            mBinding.rvIncomeListForDate.addItemDecoration(
                DividerItemDecoration(
                    this@IncomeCalendarActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        (mBinding.rvIncomeListForDate.listStatusTextView().layoutParams as FrameLayout.LayoutParams).topMargin =
            DimensionUtils.dip2px(this@IncomeCalendarActivity, 80f)
        mBinding.rvIncomeListForDate.adapter = mIncomeListAdapter
        mBinding.rvIncomeListForDate.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<ProfitStatisticsEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out ProfitStatisticsEntity>?) -> Unit
                ) {
                    mViewModel.requestIncomeListForDay(page, pageSize, callBack)
                }
            }
    }

    override fun initData() {
    }
}