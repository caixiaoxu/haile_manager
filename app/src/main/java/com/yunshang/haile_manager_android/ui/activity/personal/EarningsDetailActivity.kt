package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.EarningsDetailViewModel
import com.yunshang.haile_manager_android.business.vm.IncomeDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo
import com.yunshang.haile_manager_android.databinding.ActivityIncomeDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeDetailInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

class EarningsDetailActivity :
    BaseBusinessActivity<ActivityIncomeDetailBinding, EarningsDetailViewModel>(
        EarningsDetailViewModel::class.java,
    ) {

    companion object {
        const val IncomeId = "incomeId"
    }

    override fun layoutId(): Int = R.layout.activity_income_detail

    override fun backBtn(): View = mBinding.barIncomeDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.incomeId = intent.getIntExtra(IncomeId, -1)
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
                }
            }
        }
    }

    override fun initView() {
        mBinding.barIncomeDetailTitle.getTitle().text =
            StringUtils.getString(R.string.earnings_detail)

        mBinding.flIncomeDetailCheckOrder.setOnClickListener {
            if (!UserPermissionUtils.hasOrderInfoPermission()) {
                SToast.showToast(
                    this@EarningsDetailActivity,
                    resources.getString(R.string.permission_err_order_info)
                )
                return@setOnClickListener
            }
            mViewModel.incomeDetail.value?.let { detail ->
                startActivity(
                    if ("1000" == detail.category) {
                        Intent(
                            this@EarningsDetailActivity,
                            IncomeDetailActivity::class.java
                        ).apply {
                            putExtra(IncomeDetailActivity.DetailType, 1)
                            putExtra(IncomeDetailActivity.OrderNo, detail.orderNo)
                        }
                    } else {
                        Intent(
                            this@EarningsDetailActivity,
                            OrderDetailActivity::class.java
                        ).apply {
                            putExtras(IntentParams.OrderDetailParams.pack(detail.orderId))
                        }
                    })
            }
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}