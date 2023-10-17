package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.AppointSetting
import com.yunshang.haile_manager_android.data.entities.GoodsSetting
import com.yunshang.haile_manager_android.databinding.ActivityShopDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemShopDetailAppointmentBinding
import com.yunshang.haile_manager_android.databinding.ItemShopDetailPayConfigBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class ShopDetailActivity : BaseBusinessActivity<ActivityShopDetailBinding, ShopDetailViewModel>(
    ShopDetailViewModel::class.java,
    BR.vm
) {
    companion object {
        const val ShopId = "ShopId"
    }

    override fun layoutId(): Int = R.layout.activity_shop_detail

    override fun backBtn(): View = mBinding.barShopDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopId = intent.getIntExtra(ShopId, -1)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvShopDetailPayConfigTitle.setOnClickListener {
            mViewModel.shopDetail.value?.paymentSettings?.let {
                val show = View.VISIBLE == mBinding.llShopDetailPayConfigInfoParent.visibility
                mBinding.llShopDetailPayConfigInfoParent.visibility =
                    if (show) View.GONE else View.VISIBLE
                mBinding.tvShopDetailPayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (!show) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                    0
                )
            }
        }

        mBinding.tvShopDetailAppointmentInfoTitle.setOnClickListener {
            if (mViewModel.shopDetail.value?.appointSettingList.isNullOrEmpty()) return@setOnClickListener
            (View.VISIBLE == mBinding.groupShopDetailAppointmentInfo.visibility).let { show ->
                if (show) it.setBackgroundColor(Color.WHITE) else it.setBackgroundResource(R.drawable.shape_bottom_stroke_dividing_mlr12)
                mBinding.groupShopDetailAppointmentInfo.visibility =
                    if (show) View.GONE else View.VISIBLE
                mBinding.tvShopDetailAppointmentInfoStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    if (!show) R.drawable.icon_arrow_down_with_padding else R.drawable.icon_arrow_right_with_padding,
                    0
                )
            }
        }

        // 设置间距
        mBinding.llShopDetailAppointmentInfo.space = DimensionUtils.dip2px(this, 12f)

        // 复制
        mBinding.tvShopDetailServicePhoneCopy.setOnClickListener {
            (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).run {
                setPrimaryClip(
                    ClipData.newPlainText(
                        "",
                        mViewModel.shopDetail.value?.serviceTelephone ?: ""
                    )
                )
            }
            SToast.showToast(this@ShopDetailActivity, "已复制到剪切板")
        }

        // 运营设置
        mBinding.btnShopDetailOperationSetting.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopDetailActivity,
                    ShopOperationSettingActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopOperationSettingParams.pack(
                            mViewModel.shopDetail.value?.operationSettings?.volumeVisibleState,
                            mViewModel.shopId
                        )
                    )
                }
            )
        }

        // 预约
        mBinding.btnShopDetailAppointment.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopDetailActivity,
                    ShopAppointmentSettingActivity::class.java
                ).apply {
                    putExtra(ShopAppointmentSettingActivity.ShopId, mViewModel.shopId)
                }
            )
        }

        // 编辑
        mBinding.btnShopDetailEdit.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopDetailActivity,
                    ShopCreateAndUpdateActivity::class.java
                ).apply {
                    putExtra(
                        ShopCreateAndUpdateActivity.ShopDetail,
                        GsonUtils.any2Json(mViewModel.shopDetail.value!!)
                    )
                })
        }

        // 删除
        mBinding.btnShopDetailDelete.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.shop_delete_hint)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.delete)) {
                    mViewModel.requestShopDelete()
                }
            }.build()
                .show(supportFragmentManager)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasShopAppointPermission.observe(this) {
            mBinding.btnShopDetailAppointment.visibility = if (it) View.VISIBLE else View.GONE
        }

        mSharedViewModel.hasShopUpdatePermission.observe(this) {
            mBinding.btnShopDetailEdit.visibility = if (it) View.VISIBLE else View.GONE
        }

        mSharedViewModel.hasShopDeletePermission.observe(this) {
            mBinding.btnShopDetailDelete.visibility =
                if (it) View.VISIBLE else View.GONE
        }

        mViewModel.shopDetail.observe(this) {
            // 刷新支付设置布局
            mBinding.llShopDetailPayConfigInfo.buildChild<ItemShopDetailPayConfigBinding, GoodsSetting>(
                it.paymentSettings?.goodsSettingList
            ) { _, childBinding, data ->
                val content = data.getOpenSettings()
                childBinding.root.visibility = if (content.isEmpty()) View.GONE else View.VISIBLE
                childBinding.content = data.goodsCategoryName + "：" + content
            }
            // 刷新预约布局
            if (null != it.appointSettingList) {
                val noAllClose = it.appointSettingList.any { setting -> 0 != setting.appointSwitch }
                mBinding.tvShopDetailAppointmentInfoStatus.setText(if (noAllClose) R.string.open else R.string.close)
                mBinding.tvShopDetailAppointmentInfoStatus.setTextColor(
                    ContextCompat.getColor(
                        this@ShopDetailActivity,
                        if (noAllClose) R.color.colorPrimary else R.color.common_sub_txt_color
                    )
                )
                mBinding.llShopDetailAppointmentInfo.buildChild<ItemShopDetailAppointmentBinding, AppointSetting>(
                    it.appointSettingList, show = false
                ) { _, childBinding, data ->
                    childBinding.tvShopDetailsAppointmentName.text =
                        data.goodsCategoryName + StringUtils.getString(R.string.appointment)
                    childBinding.tvShopDetailsAppointmentValue.text =
                        StringUtils.getString(if (0 == data.appointSwitch) R.string.out_of_service else R.string.in_use)
                    childBinding.tvShopDetailsAppointmentValue.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            if (0 == data.appointSwitch) R.color.common_sub_txt_color else R.color.colorPrimary,
                            null
                        )
                    )
                }
            }
        }

        // 修改成功后
        LiveDataBus.with(BusEvents.SHOP_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestShopDetail()
        }

        mViewModel.jump.observe(this) {
            finish()
        }

    }

    override fun initData() {
        mViewModel.requestShopDetail()
    }
}