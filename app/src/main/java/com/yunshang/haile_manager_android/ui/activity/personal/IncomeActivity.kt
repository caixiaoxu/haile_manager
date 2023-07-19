package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeViewModel
import com.yunshang.haile_manager_android.data.entities.IncomeListByDayEntity
import com.yunshang.haile_manager_android.data.rule.ICalendarEntity
import com.yunshang.haile_manager_android.databinding.ActivityIncomeBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeCalendarBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeListByDayBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.GridSpaceItemDecoration
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.ViewUtils
import timber.log.Timber
import java.util.*

class IncomeActivity : BaseBusinessActivity<ActivityIncomeBinding, IncomeViewModel>(
    IncomeViewModel::class.java,
    BR.vm
) {

    companion object {
        const val ProfitType = "profitType"
        const val ProfitSearchId = "profitSearchId"
        const val DeviceName = "deviceName"
        const val SelectDay = "selectDay"
    }

    private val mIncomeAdapter: CommonRecyclerAdapter<ItemIncomeCalendarBinding, ICalendarEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_calendar,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                if (-1 == item.type) return@setOnClickListener
                mViewModel.selectDay.value = item.getDate()
            }
            mViewModel.selectDay.observe(this) { day ->
                mItemBinding?.root?.setBackgroundColor(
                    ContextCompat.getColor(
                        this@IncomeActivity,
                        if (item.isSelect(day)) R.color.colorPrimary else R.color.white
                    )
                )
                mItemBinding?.tvIncomeCalendarDayNum?.setTextColor(
                    ContextCompat.getColor(
                        this@IncomeActivity,
                        if (item.isSelect(day)) R.color.white else R.color.common_txt_color
                    )
                )
                mItemBinding?.tvIncomeCalendarDayAmount?.setTextColor(
                    if (item.isSelect(day)) Color.WHITE else item.curTypeColor
                )
            }
        }
    }
    private val mIncomeListAdapter: CommonRecyclerAdapter<ItemIncomeListByDayBinding, IncomeListByDayEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_income_list_by_day,
            BR.item
        ) { mItemBinding, _, item ->
            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@IncomeActivity,
                        EarningsDetailActivity::class.java
                    ).apply {
                        putExtra(EarningsDetailActivity.IncomeId, item.id)
                    })
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_income

    override fun backBtn(): View = mBinding.barIncomeTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.profitType = intent.getIntExtra(ProfitType, 3)
        mViewModel.profitSearchId = intent.getIntExtra(ProfitSearchId, -1)
        mViewModel.deviceName = intent.getStringExtra(DeviceName) ?: ""

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
        }
        mViewModel.calendarIncome.observe(this) {
            mIncomeAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

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
        mBinding.rvIncomeCalendar.addItemDecoration(
            GridSpaceItemDecoration(
                DimensionUtils.dip2px(this@IncomeActivity, 1f),
                DimensionUtils.dip2px(this@IncomeActivity, 1f),
            )
        )
        mBinding.rvIncomeCalendar.adapter = mIncomeAdapter

        mBinding.rvIncomeListForDate.layoutManager = LinearLayoutManager(this)
        mBinding.rvIncomeListForDate.enableRefresh = false
        ResourcesCompat.getDrawable(resources, R.drawable.divder_efefef_size_half, null)?.let {
            mBinding.rvIncomeListForDate.addItemDecoration(
                DividerItemDecoration(
                    this@IncomeActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvIncomeListForDate.adapter = mIncomeListAdapter
        mBinding.rvIncomeListForDate.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<IncomeListByDayEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out IncomeListByDayEntity>?) -> Unit
                ) {
                    mViewModel.requestIncomeListForDay(page, pageSize, callBack)
                }
            }
    }

    override fun initData() {
    }
}