package com.shangyun.haile_manager_android.ui.activity.personal

import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.IncomeDetailViewModel
import com.shangyun.haile_manager_android.databinding.ActivityIncomeDetailBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class IncomeDetailActivity :
    BaseBusinessActivity<ActivityIncomeDetailBinding, IncomeDetailViewModel>(
        IncomeDetailViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val IncomeId = "incomeId"
    }

    override fun layoutId(): Int = R.layout.activity_income_detail

    override fun initIntent() {
        super.initIntent()
        mViewModel.incomeId = intent.getIntExtra(IncomeId, -1)
    }

    override fun initView() {
    }

    override fun initData() {
    }
}