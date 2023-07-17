package com.yunshang.haile_manager_android.ui.activity.personal

import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IncomeDetailViewModel
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo
import com.yunshang.haile_manager_android.databinding.ActivityIncomeDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeDetailInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class IncomeDetailActivity :
    BaseBusinessActivity<ActivityIncomeDetailBinding, IncomeDetailViewModel>(
        IncomeDetailViewModel::class.java,
    ) {

    companion object {
        const val DetailType = "detailType"
        const val IncomeId = "incomeId"
        const val OrderNo = "orderNo"
    }

    override fun layoutId(): Int = R.layout.activity_income_detail

    override fun backBtn(): View = mBinding.barIncomeDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.detailType = intent.getIntExtra(DetailType, 0)
        mViewModel.incomeId = intent.getIntExtra(IncomeId, -1)
        mViewModel.orderNo = intent.getStringExtra(OrderNo)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.incomeDetail.observe(this) {
            it?.let {
                mBinding.detail = it
                mBinding.llIncomeDetailInfos.buildChild<ItemIncomeDetailInfoBinding, IncomeDetailInfo>(
                    it.getInfoList()
                ) { _, childBinding, data ->
                    childBinding.title = data.title
                    childBinding.value = data.value
                    childBinding.canCopy = data.canCopy

                    if (data.canCopy) {
                        childBinding.tvItemIncomeDetailCopy.setOnClickListener {
                            com.yunshang.haile_manager_android.utils.StringUtils.copyToShear(data.value)
                        }
                    }
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