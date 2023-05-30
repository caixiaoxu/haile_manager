package com.shangyun.haile_manager_android.ui.activity.personal

import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.IncomeDetailViewModel
import com.shangyun.haile_manager_android.data.rule.IncomeDetailInfo
import com.shangyun.haile_manager_android.databinding.ActivityIncomeDetailBinding
import com.shangyun.haile_manager_android.databinding.ItemIncomeDetailInfoBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class IncomeDetailActivity :
    BaseBusinessActivity<ActivityIncomeDetailBinding, IncomeDetailViewModel>(
        IncomeDetailViewModel::class.java,
    ) {

    companion object {
        const val IncomeId = "incomeId"
        const val OrderNo = "orderNo"
    }

    override fun layoutId(): Int = R.layout.activity_income_detail

    override fun backBtn(): View = mBinding.barIncomeDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.incomeId = intent.getIntExtra(IncomeId, -1)
        mViewModel.orderNo = intent.getStringExtra(OrderNo)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.rechargeDetail.observe(this) {
            it?.let {
                mBinding.detail = it
                mBinding.llIncomeDetailInfos.buildChild<ItemIncomeDetailInfoBinding, IncomeDetailInfo>(
                    it.getInfoList()
                ) { _, childBinding, data ->
                    childBinding.title = data.title
                    childBinding.value = data.value
                    childBinding.canCopy = data.canCopy
                }
            }
        }
    }

    override fun initView() {
        mBinding.barIncomeDetailTitle.getTitle().text =
            StringUtils.getString(R.string.income_detail)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}