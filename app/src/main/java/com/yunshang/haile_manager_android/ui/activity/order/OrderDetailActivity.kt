package com.yunshang.haile_manager_android.ui.activity.order

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.OrderDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.Promotion
import com.yunshang.haile_manager_android.data.entities.Sku
import com.yunshang.haile_manager_android.databinding.ActivityOrderDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemOrderDetailInfoBinding
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

                mBinding.llOrderDetailInfoDeviceNames.buildChild<ItemOrderDetailInfoBinding, Sku>(
                    orderDetail.skuDeviceTypes
                ) { index, childBinding, data ->
                    childBinding.itemTitle =
                        if (0 == index) "${StringUtils.getString(R.string.device_info)}：" else ""
                    childBinding.content = "${data.goodsName}（设备编号：${data.goodsId}）"
                    childBinding.canShow = true
                }

                mBinding.llOrderDetailInfoDeviceImeis.buildChild<ItemOrderDetailInfoBinding, Sku>(
                    orderDetail.skuDeviceTypes
                ) { index, childBinding, data ->
                    childBinding.itemTitle =
                        if (0 == index) "${StringUtils.getString(R.string.imei)}：" else ""
                    childBinding.content = data.imei
                    childBinding.canShow = true
                    childBinding.showCopy = true
                }

                mBinding.llOrderDetailSkuList.buildChild<ItemOrderDetailInfoBinding, Sku>(
                    orderDetail.skuList
                ) { index, childBinding, data ->
                    childBinding.itemTitle =
                        if (0 == index) "${StringUtils.getString(R.string.consumption_item)}：" else ""
                    childBinding.content =
                        "${data.skuName}/${data.unitValue} ￥${data.originUnitPrice}"
                    childBinding.canShow = true
                    childBinding.showCopy = true
                }

                mBinding.llOrderDetailCouponList.buildChild<ItemOrderDetailInfoBinding, Promotion>(
                    orderDetail.promotionList
                ) { _, childBinding, data ->
                    childBinding.itemTitle = "${data.title}："
                    childBinding.content = "-￥${data.discountPrice}"
                    childBinding.canShow = true
                    childBinding.showCopy = true
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemOrderDetailInfoRealIncome.tvItemOrderDetailInfoContent.setTextColor(
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
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${mViewModel.orderDetail.value?.buyerPhone}")
            startActivity(intent)
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestOrderDetail()
    }
}