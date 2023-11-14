package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WithdrawDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo
import com.yunshang.haile_manager_android.databinding.ActivityIncomeDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeDetailInfoBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeDetailWithdrawInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class WithdrawDetailActivity :
    BaseBusinessActivity<ActivityIncomeDetailBinding, WithdrawDetailViewModel>(
        WithdrawDetailViewModel::class.java,
    ) {

    override fun layoutId(): Int = R.layout.activity_income_detail

    override fun backBtn(): View = mBinding.barIncomeDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.id = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.withDrawViewModel.observe(this) {
            it?.let {
                mBinding.detail = it
                mBinding.llIncomeDetailWithdrawInfos.buildChild<ItemIncomeDetailWithdrawInfoBinding, IncomeDetailInfo>(
                    it.getWithdrawInfoList()
                ) { _, childBinding, data ->
                    childBinding.title = data.title
                    childBinding.value = data.value
                }
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
        window.statusBarColor = Color.WHITE
        mBinding.barIncomeDetailTitle.setBackgroundColor(Color.WHITE)
        mBinding.barIncomeDetailTitle.getTitle().text =
            StringUtils.getString(R.string.withdraw_detail)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}