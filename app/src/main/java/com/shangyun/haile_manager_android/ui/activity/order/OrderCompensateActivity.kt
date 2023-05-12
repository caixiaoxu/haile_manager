package com.shangyun.haile_manager_android.ui.activity.order

import android.graphics.Color
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.OrderCompensateViewModel
import com.shangyun.haile_manager_android.databinding.ActivityOrderCompensateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class OrderCompensateActivity :
    BaseBusinessActivity<ActivityOrderCompensateBinding, OrderCompensateViewModel>() {

    private val mOrderCompensateViewModel by lazy {
        getActivityViewModelProvider(this)[OrderCompensateViewModel::class.java]
    }

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

    override fun getVM(): OrderCompensateViewModel = mOrderCompensateViewModel

    override fun initIntent() {
        super.initIntent()
        // 面额
        mOrderCompensateViewModel.couponAmount = intent.getDoubleExtra(CouponAmount, 0.0)
        // 适用门店
        mOrderCompensateViewModel.couponShopId = intent.getIntExtra(CouponShopId, -1)
        mOrderCompensateViewModel.couponShopName = intent.getStringExtra(CouponShopName)
        //适用设备类型
        mOrderCompensateViewModel.couponDeviceTypeIds =
            intent.getIntegerArrayListExtra(CouponDeviceTypeIds)
        mOrderCompensateViewModel.couponDeviceTypeName =
            intent.getStringExtra(CouponDeviceTypeName)

        mOrderCompensateViewModel.buyerId = intent.getIntExtra(BuyerId, -1)
        mOrderCompensateViewModel.buyerPhone = intent.getStringExtra(BuyerPhone)
        mOrderCompensateViewModel.orderNo = intent.getStringExtra(OrderNo)
    }

    override fun initEvent() {
        super.initEvent()
        mOrderCompensateViewModel.jump.observe(this) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
        mBinding.vm = mOrderCompensateViewModel
    }
}