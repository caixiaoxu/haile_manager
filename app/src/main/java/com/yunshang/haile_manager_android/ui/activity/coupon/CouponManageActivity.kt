package com.yunshang.haile_manager_android.ui.activity.coupon

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.CouponManageViewModel
import com.yunshang.haile_manager_android.databinding.ActivityCouponManageBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class CouponManageActivity :
    BaseBusinessActivity<ActivityCouponManageBinding, CouponManageViewModel>(
        CouponManageViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_coupon_manage

    override fun backBtn(): View = mBinding.barCouponManageTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}