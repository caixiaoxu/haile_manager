package com.yunshang.haile_manager_android.ui.activity.coupon

import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.CouponDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityCouponDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class CouponDetailActivity :
    BaseBusinessActivity<ActivityCouponDetailBinding, CouponDetailViewModel>(
        CouponDetailViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_coupon_detail

    override fun backBtn(): View = mBinding.barCouponDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.couponId = IntentParams.CommonParams.parseId(intent)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnCouponDetailDelete.setOnClickListener {
            CommonDialog.Builder("是否作废").apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.cancellation)) {
                    mViewModel.abandonCoupon(this@CouponDetailActivity)
                }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
        mViewModel.requestCouponDetail()
    }
}