package com.shangyun.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.IncomeViewModel
import com.shangyun.haile_manager_android.data.rule.ICalendarEntity
import com.shangyun.haile_manager_android.databinding.ActivityIncomeBinding
import com.shangyun.haile_manager_android.databinding.ItemIncomeCalendarBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.GridSpaceItemDecoration
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.utils.ViewUtils

class IncomeActivity : BaseBusinessActivity<ActivityIncomeBinding, IncomeViewModel>() {

    private val mIncomeViewModel by lazy {
        getActivityViewModelProvider(this)[IncomeViewModel::class.java]
    }

    companion object {
        const val ProfitType = "profitType"
        const val ProfitSearchId = "profitSearchId"
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemIncomeCalendarBinding, ICalendarEntity>(
            R.layout.item_income_calendar,
            BR.item
        ) { mItemBinding, pos, item ->

        }
    }

    override fun layoutId(): Int = R.layout.activity_income

    override fun getVM(): IncomeViewModel = mIncomeViewModel

    override fun backBtn(): View = mBinding.barIncomeTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mIncomeViewModel.profitType = intent.getIntExtra(ProfitType, 3)
        mIncomeViewModel.profitSearchId = intent.getIntExtra(ProfitSearchId, -1)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
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
                DimensionUtils.dip2px(this@IncomeActivity, 0.5f),
                DimensionUtils.dip2px(this@IncomeActivity, 0.5f)
            )
        )
        mBinding.rvIncomeCalendar.adapter = mAdapter
    }

    override fun initData() {
        mBinding.vm = mIncomeViewModel
        mIncomeViewModel.requestTotalForDay()
        mIncomeViewModel.requestIncomeByDate()
    }
}