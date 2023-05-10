package com.shangyun.haile_manager_android.ui.activity.order

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.OrderDetailViewModel
import com.shangyun.haile_manager_android.databinding.ActivityOrderDetailBinding
import com.shangyun.haile_manager_android.databinding.ItemOrderDetailItemBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class OrderDetailActivity :
    BaseBusinessActivity<ActivityOrderDetailBinding, OrderDetailViewModel>() {

    private val mOrderDetailViewModel by lazy {
        getActivityViewModelProvider(this)[OrderDetailViewModel::class.java]
    }

    companion object {
        const val OrderId = "orderId"
    }

    override fun layoutId(): Int = R.layout.activity_order_detail

    override fun getVM(): OrderDetailViewModel = mOrderDetailViewModel

    override fun initIntent() {
        super.initIntent()
        mOrderDetailViewModel.orderId = intent.getIntExtra(OrderId, -1)
    }

    override fun initEvent() {
        super.initEvent()

        mOrderDetailViewModel.orderDetail.observe(this) {
            it?.let { orderDetail ->
                mBinding.llOrderDetailSkuList.removeAllViews()
                orderDetail.skuList.forEach { sku ->
                    DataBindingUtil.inflate<ItemOrderDetailItemBinding>(
                        LayoutInflater.from(this@OrderDetailActivity),
                        R.layout.item_order_detail_item,
                        null,
                        false
                    ).also { itemBinding ->
                        itemBinding.title = "${sku.skuName}/${sku.skuUnit}分钟："
                        itemBinding.value = "￥${sku.originUnitPrice}"
                        itemBinding.mStart =
                            DimensionUtils.dip2px(this@OrderDetailActivity, 8f).toFloat()
                        mBinding.llOrderDetailSkuList.addView(
                            itemBinding.root,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }
                }
                mBinding.llOrderDetailCouponList.removeAllViews()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
        mBinding.vm = mOrderDetailViewModel

        mOrderDetailViewModel.requestOrderDetail()
    }
}