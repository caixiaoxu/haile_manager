package com.yunshang.haile_manager_android.ui.activity.order

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.OrderCompensateViewModel
import com.yunshang.haile_manager_android.databinding.ActivityOrderCompensateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class OrderCompensateActivity :
    BaseBusinessActivity<ActivityOrderCompensateBinding, OrderCompensateViewModel>(
        OrderCompensateViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val CouponAmount = "coupon_amount"
        const val CouponShopId = "coupon_shopId"
        const val CouponShopName = "coupon_shop_name"
        const val CouponDeviceTypeIds = "coupon_device_type_ids"
        const val CouponDeviceTypeName = "coupon_device_type_name"
        const val BuyerId = "buyerId"
        const val BuyerPhone = "buyerPhone"
        const val OrderNo = "orderNo"
    }

    override fun layoutId(): Int = R.layout.activity_order_compensate

    override fun backBtn(): View = mBinding.barOrderCompensateTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        // 面额
        mViewModel.couponAmount = intent.getDoubleExtra(CouponAmount, 0.0)
        // 适用门店
        mViewModel.couponShopId = intent.getIntExtra(CouponShopId, -1)
        mViewModel.couponShopName = intent.getStringExtra(CouponShopName)
        //适用设备类型
        mViewModel.couponDeviceTypeIds =
            intent.getIntegerArrayListExtra(CouponDeviceTypeIds)
        mViewModel.couponDeviceTypeName =
            intent.getStringExtra(CouponDeviceTypeName)

        mViewModel.buyerId = intent.getIntExtra(BuyerId, -1)
        mViewModel.buyerPhone = intent.getStringExtra(BuyerPhone)
        mViewModel.orderNo = intent.getStringExtra(OrderNo)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}