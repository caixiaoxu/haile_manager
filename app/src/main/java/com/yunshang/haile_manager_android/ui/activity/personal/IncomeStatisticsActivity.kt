package com.yunshang.haile_manager_android.ui.activity.personal

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeStatisticsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityIncomeStatisticsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class IncomeStatisticsActivity : BaseBusinessActivity<ActivityIncomeStatisticsBinding, IncomeStatisticsViewModel>(
    IncomeStatisticsViewModel::class.java,BR.vm
) {

    override fun layoutId(): Int =R.layout.activity_income_statistics

    override fun initView() {
    }

    override fun initData() {
    }
}