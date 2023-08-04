package com.yunshang.haile_manager_android.ui.activity.order

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.OrderDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.databinding.ActivityOrderDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemOrderDetailItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils


class OrderDetailActivity :
    BaseBusinessActivity<ActivityOrderDetailBinding, OrderDetailViewModel>(
        OrderDetailViewModel::class.java,
        BR.vm
    ) {

    // 补偿界面界面
    private val startCompensateSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                mViewModel.orderDetail.value?.canCompensate = false
            }
        }

    override fun layoutId(): Int = R.layout.activity_order_detail

    override fun backBtn(): View = mBinding.barOrderDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.orderId = IntentParams.OrderDetailParams.parseOrderId(intent)
        mViewModel.isAppoint = IntentParams.OrderDetailParams.parseIsAppoint(intent)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.orderDetail.observe(this) {
            it?.let { orderDetail ->
                if (mViewModel.isAppoint) {
                    DateTimeUtils.formatDateFromString(orderDetail.appointmentInfo?.appointmentUsageTime)
                        ?.let { date ->
                            val dateStr = DateTimeUtils.formatDateTime(date, "yyyy/MM/dd（E）HH:mm")
                            mBinding.tvOrderDetailStatas.text =
                                com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                                    StringUtils.getString(R.string.appoint_dealLine, dateStr),
                                    arrayOf(
                                        ForegroundColorSpan(
                                            ContextCompat.getColor(
                                                this@OrderDetailActivity,
                                                R.color.colorPrimary
                                            )
                                        ), 3, 3 + dateStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                                    ),
                                    3, 3 + dateStr.length

                                )
                            mBinding.tvOrderDetailStatas.visibility = View.VISIBLE
                        }
                }

                mBinding.llOrderDetailSkuList.removeAllViews()
                mBinding.llOrderDetailSkuList.visibility =
                    if (orderDetail.skuList.isNullOrEmpty()) View.GONE else View.VISIBLE
                val mStart =
                    DimensionUtils.dip2px(this@OrderDetailActivity, 8f).toFloat()
                orderDetail.skuList.forEach { sku ->
                    DataBindingUtil.inflate<ItemOrderDetailItemBinding>(
                        LayoutInflater.from(this@OrderDetailActivity),
                        R.layout.item_order_detail_item,
                        null,
                        false
                    ).also { itemBinding ->
                        itemBinding.title =
                            "${sku.skuName}/${
                                if (DeviceCategory.isDrinking(sku.goodsCategoryCode)) {
                                    if (1 == sku.goodsItemInfo?.priceCalculateMode) "${sku.skuUnit / 1000.0}L" else "${sku.skuUnit}S"
                                } else "${sku.skuUnit}分钟"
                            }："
                        itemBinding.value = "￥${sku.originUnitPrice}"
                        itemBinding.mStart = mStart
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
                mBinding.llOrderDetailCouponList.visibility =
                    if (orderDetail.promotionList.isNullOrEmpty()) View.GONE else View.VISIBLE
                orderDetail.promotionList.forEach { promotion ->
                    // 金额大于0添加
                    if (promotion.discountPrice > 0) {
                        DataBindingUtil.inflate<ItemOrderDetailItemBinding>(
                            LayoutInflater.from(this@OrderDetailActivity),
                            R.layout.item_order_detail_item,
                            null,
                            false
                        ).also { itemBinding ->
                            itemBinding.title = "${promotion.title}："
                            itemBinding.value = "-￥${promotion.discountPrice}"
                            itemBinding.mStart = mStart
                            mBinding.llOrderDetailCouponList.addView(
                                itemBinding.root,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.includeOrderDetailRealPrice.tvOrderDetailTotalAmount.setTextColor(
            ContextCompat.getColor(
                this@OrderDetailActivity,
                R.color.colorPrimary
            )
        )

        // 退款
        mBinding.tvOrderDetailRefund.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.refund_hint)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.sure)) {
                    mViewModel.orderOperate(this@OrderDetailActivity, 0)
                }
            }.build()
                .show(supportFragmentManager)
        }
        // 补偿
        mBinding.tvOrderDetailCompensate.setOnClickListener {
            mViewModel.orderDetail.value?.let { detial ->
                startCompensateSelect.launch(
                    Intent(
                        this@OrderDetailActivity,
                        OrderCompensateActivity::class.java
                    ).apply {
                        putExtra(OrderCompensateActivity.CouponAmount, detial.originPrice)
                        putExtra(OrderCompensateActivity.CouponShopId, detial.shopId)
                        putExtra(OrderCompensateActivity.CouponShopName, detial.shopName)
                        putIntegerArrayListExtra(
                            OrderCompensateActivity.CouponDeviceTypeIds,
                            detial.goodsCategoryIds
                        )
                        putExtra(OrderCompensateActivity.CouponDeviceTypeName, detial.deviceType)
                        putExtra(OrderCompensateActivity.BuyerId, detial.buyerId)
                        putExtra(OrderCompensateActivity.BuyerPhone, detial.buyerPhone)
                        putExtra(OrderCompensateActivity.OrderNo, detial.orderNo)
                    })
            }
        }
        // 启动
        mBinding.tvOrderDetailStart.setOnClickListener {
            mViewModel.orderOperate(
                this@OrderDetailActivity,
                1
            )
        }
        // 复位
        mBinding.tvOrderDetailRestart.setOnClickListener {
            mViewModel.orderOperate(
                this@OrderDetailActivity,
                2
            )
        }
        // 拨打电话
        mBinding.tvOrderDetailBuyerPhoneCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:${mViewModel.orderDetail.value?.buyerPhone}")
            startActivity(intent)
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestOrderDetail()
    }
}