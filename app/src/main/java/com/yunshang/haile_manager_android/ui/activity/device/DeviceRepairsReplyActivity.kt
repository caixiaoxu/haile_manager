package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.ScreenUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsReplyViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.InvoiceReceiverEntity
import com.yunshang.haile_manager_android.data.entities.ReplyDTOS
import com.yunshang.haile_manager_android.data.rule.CommonDialogItemParam
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsReplyBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsReplyBinding
import com.yunshang.haile_manager_android.databinding.ItemIssueInvoiceReceiverSelectBinding
import com.yunshang.haile_manager_android.databinding.ItemRepairsPhoneOperateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.PicBrowseActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderManagerActivity
import com.yunshang.haile_manager_android.ui.view.RoundImageView
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.loadImage
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewBottomSheetDialog
import com.yunshang.haile_manager_android.utils.StringUtils

class DeviceRepairsReplyActivity :
    BaseBusinessActivity<ActivityDeviceRepairsReplyBinding, DeviceRepairsReplyViewModel>(
        DeviceRepairsReplyViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_repairs_reply

    override fun backBtn(): View = mBinding.barDeviceRepairsReplyTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.id = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.repairsDetails.observe(this) {
            it?.let { detail ->

                mBinding.tvDeviceRepairsReplyUser.movementMethod = LinkMovementMethod.getInstance()
                mBinding.tvDeviceRepairsReplyUser.highlightColor = Color.TRANSPARENT
                mBinding.tvDeviceRepairsReplyUser.text =
                    (it.userAccount?.let { phone ->
                        StringUtils.formatMultiStyleStr(
                            phone, arrayOf(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        this@DeviceRepairsReplyActivity,
                                        R.color.colorPrimary
                                    )
                                ),
                                object : ClickableSpan() {
                                    override fun onClick(view: View) {
                                        showPhoneOperateDialog(phone)
                                    }

                                    override fun updateDrawState(ds: TextPaint) {
                                        //去掉下划线
                                        ds.isUnderlineText = false
                                    }
                                },
                            ), 0, phone.length
                        )
                    } ?: "")

                mBinding.tvDeviceRepairsReplyDeviceName.movementMethod =
                    LinkMovementMethod.getInstance()
                mBinding.tvDeviceRepairsReplyDeviceName.highlightColor = Color.TRANSPARENT
                mBinding.tvDeviceRepairsReplyDeviceName.text =
                    (it.deviceName?.let { deviceName ->
                        StringUtils.formatMultiStyleStr(
                            deviceName, arrayOf(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        this@DeviceRepairsReplyActivity,
                                        R.color.colorPrimary
                                    )
                                ),
                                object : ClickableSpan() {
                                    override fun onClick(view: View) {
                                        startActivity(
                                            Intent(
                                                this@DeviceRepairsReplyActivity,
                                                DeviceDetailActivity::class.java
                                            ).apply {
                                                putExtra(
                                                    DeviceDetailActivity.GoodsId,
                                                    mViewModel.repairsDetails.value?.goodsId
                                                )
                                            }
                                        )
                                    }

                                    override fun updateDrawState(ds: TextPaint) {
                                        //去掉下划线
                                        ds.isUnderlineText = false
                                    }
                                },
                            ), 0, deviceName.length
                        )
                    } ?: "")

                mBinding.clDeviceRepairsReplyFaultPic.removeViews(
                    1,
                    mBinding.clDeviceRepairsReplyFaultPic.childCount - 1
                )
                val picItemWH = (ScreenUtils.screenWidth - DimensionUtils.dip2px(
                    this@DeviceRepairsReplyActivity,
                    16f
                ) * 2 - DimensionUtils.dip2px(this@DeviceRepairsReplyActivity, 12f) * 3) / 4
                if (detail.pics.isNullOrEmpty()) {
                    mBinding.clDeviceRepairsReplyFaultPic.visibility(false)
                } else {
                    mBinding.clDeviceRepairsReplyFaultPic.visibility(true)
                    val picItemRadius =
                        DimensionUtils.dip2px(this@DeviceRepairsReplyActivity, 12f)
                    detail.pics.forEachIndexed { index, url ->
                        mBinding.clDeviceRepairsReplyFaultPic.addView(RoundImageView(this@DeviceRepairsReplyActivity).apply {
                            radius = picItemRadius.toFloat()
                            id = index + 1
                            loadImage(imgHeadUrl = url)
                            setOnClickListener {
                                startActivity(
                                    Intent(
                                        this@DeviceRepairsReplyActivity,
                                        PicBrowseActivity::class.java
                                    ).apply {
                                        putExtras(IntentParams.PicParams.pack(url))
                                    })
                            }
                        }, picItemWH, picItemWH)
                    }
                    mBinding.flowDeviceRepairsReplyFaultPic.referencedIds =
                        IntArray(detail.pics.size) { item -> item + 1 }
                }

                mBinding.llDeviceRepairsReplyList.buildChild<ItemDeviceRepairsReplyBinding, ReplyDTOS>(
                    it.replyDTOS,
                    start = 1
                ) { _, childBinding, data ->
                    childBinding.child = data
                }
            }
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    private fun showPhoneOperateDialog(phone: String) {
        val list = listOf(
            CommonDialogItemParam(0, "拨号"),
            CommonDialogItemParam(1, "复制"),
            CommonDialogItemParam(2, "用户订单")
        )
        CommonNewBottomSheetDialog.Builder<CommonDialogItemParam, ItemRepairsPhoneOperateBinding>(
            phone,
            list,
            lp = LayoutParams(
                LayoutParams.MATCH_PARENT,
                DimensionUtils.dip2px(this@DeviceRepairsReplyActivity, 52f)
            ),
            buildItemView = { _, data, _ ->
                DataBindingUtil.inflate<ItemRepairsPhoneOperateBinding?>(
                    LayoutInflater.from(this@DeviceRepairsReplyActivity),
                    R.layout.item_repairs_phone_operate,
                    null,
                    false
                ).apply {
                    this.child = data
                }
            },
            initView = { mDialogBinding, _ ->
                mDialogBinding.tvCommonNewDialogTitle.let { titleView ->
                    titleView.textSize = 14f
                    titleView.setTextColor(
                        ContextCompat.getColor(
                            this@DeviceRepairsReplyActivity,
                            R.color.color_black_45
                        )
                    )
                }
            }
        ) {
            when (list.find { item -> item.commonItemSelect }?.id) {
                0 -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${phone}")
                    startActivity(intent)
                }
                1 -> StringUtils.copyToShear(phone)
                2 -> {
                    startActivity(
                        Intent(
                            this@DeviceRepairsReplyActivity,
                            OrderManagerActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.OrderManagerParams.pack(
                                    2,
                                    phone = phone
                                )
                            )
                        })
                }
                else -> {}
            }
        }.build().show(supportFragmentManager)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.shared = mSharedViewModel
    }

    override fun initData() {
        mViewModel.requestData()
    }
}