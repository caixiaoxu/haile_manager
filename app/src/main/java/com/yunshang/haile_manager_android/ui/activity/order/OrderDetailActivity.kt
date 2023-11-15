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
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.OrderDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityOrderDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemOrderDetailItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceDetailActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CancelContentDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils


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

                DateTimeUtils.formatDateFromString(orderDetail._createTime)?.let { date ->
                    val day = (System.currentTimeMillis() - date.time) / (24 * 3600 * 1000)
                    mBinding.tvOrderDetailExecutiveLogging.visibility =
                        if (mViewModel.isAppoint || day > 7) View.GONE else View.VISIBLE
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
                            "${sku.skuName}/${sku.unitValue}："
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

        mBinding.tvOrderDetailExecutiveLogging.setOnClickListener {
            mViewModel.orderDetail.value?.let { detail ->
                startActivity(Intent(this, OrderExecutiveLoggingActivity::class.java).apply {
                    putExtras(IntentParams.OrderParams.pack(detail.id, detail.orderNo))
                })
            }
        }

        mBinding.btnOrderDetailDevice.setOnClickListener {
            mViewModel.orderDetail.value?.skuList?.firstOrNull()?.goodsId?.let { goodId ->
                if (UserPermissionUtils.hasDeviceInfoPermission()) {
                    // 设备详情
                    startActivity(
                        Intent(
                            this@OrderDetailActivity,
                            DeviceDetailActivity::class.java
                        ).apply {
                            putExtra(DeviceDetailActivity.GoodsId, goodId)
                        }
                    )
                } else {
                    SToast.showToast(this@OrderDetailActivity, R.string.no_permission)
                }
            }
        }

        // 预约取消
        mBinding.tvOrderDetailCancel.setOnClickListener {
            CancelContentDialog.Builder(
                StringUtils.getString(R.string.cancel_order),
                StringUtils.getString(R.string.cancel_order_hint)
            ).apply {
                positiveClickListener = { reason ->
                    mViewModel.orderDetail.value?.orderNo?.let { orderNo ->
                        mViewModel.cancelAppointmentOrder(this@OrderDetailActivity, orderNo, reason)
                    }
                }
            }.build().show(supportFragmentManager)
        }

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