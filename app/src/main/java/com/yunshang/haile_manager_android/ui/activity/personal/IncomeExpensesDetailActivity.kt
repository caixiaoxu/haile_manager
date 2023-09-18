package com.yunshang.haile_manager_android.ui.activity.personal

import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeExpensesDetailViewModel
import com.yunshang.haile_manager_android.databinding.ActivityIncomeExpensesDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class IncomeExpensesDetailActivity :
    BaseBusinessActivity<ActivityIncomeExpensesDetailBinding, IncomeExpensesDetailViewModel>(
        IncomeExpensesDetailViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_income_expenses_detail

    override fun initView() {
    }

    override fun initData() {
    }
}