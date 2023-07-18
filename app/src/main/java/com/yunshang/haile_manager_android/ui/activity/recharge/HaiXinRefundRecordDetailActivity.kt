package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRefundRecordDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.UserTokenCoinRefundItemRecordVO
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRefundRecordDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemRefundRecordDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.personal.RechargeActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CancelContentDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog


class HaiXinRefundRecordDetailActivity :
    BaseBusinessActivity<ActivityHaixinRefundRecordDetailBinding, HaiXinRefundRecordDetailViewModel>(
        HaiXinRefundRecordDetailViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_refund_record_detail

    override fun backBtn(): View = mBinding.barRefundRecordDetailTitle.getBackBtn()
    override fun initIntent() {
        super.initIntent()
        mViewModel.id = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.refundRecordDetail.observe(this) {
            mBinding.llRefundRecordItems.buildChild<ItemRefundRecordDetailBinding, UserTokenCoinRefundItemRecordVO>(
                it.userTokenCoinRefundItemRecordVOList
            ) { _, childBinding, data ->
                childBinding.item = data
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvRefundRefuse.setOnClickListener {
            CancelContentDialog.Builder(
                StringUtils.getString(R.string.refuse_refund),
                StringUtils.getString(R.string.refuse_refund_input_hint)
            ).apply {
                positiveClickListener = {
                    mViewModel.refuseRefund(it)
                }
            }.build().show(supportFragmentManager)
        }

        mBinding.tvRefundAgree.setOnClickListener {
            mViewModel.agreeRefund() { msg ->
                CommonDialog.Builder(msg).apply {
                    negativeTxt = StringUtils.getString(R.string.cancel)
                    setPositiveButton(StringUtils.getString(R.string.go_recharge)) {
                        startActivity(
                            Intent(
                                this@HaiXinRefundRecordDetailActivity,
                                RechargeActivity::class.java
                            )
                        )
                    }
                }.build().show(supportFragmentManager)
            }
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel
        mViewModel.requestData()
    }
}