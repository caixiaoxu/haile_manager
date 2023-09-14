package com.yunshang.haile_manager_android.ui.activity.coupon

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.CouponDetailViewModel
import com.yunshang.haile_manager_android.databinding.ActivityCouponDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class CouponDetailActivity :
    BaseBusinessActivity<ActivityCouponDetailBinding, CouponDetailViewModel>(
        CouponDetailViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_coupon_detail

    override fun backBtn(): View = mBinding.barCouponDetailTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
        mViewModel.requestCouponDetail()
    }
}