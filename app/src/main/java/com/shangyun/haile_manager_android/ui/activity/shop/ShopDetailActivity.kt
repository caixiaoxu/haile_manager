package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.ShopDetailViewModel
import com.shangyun.haile_manager_android.data.entities.AppointSetting
import com.shangyun.haile_manager_android.databinding.ActivityShopDetailBinding
import com.shangyun.haile_manager_android.databinding.ItemShopDetailAppointmentBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonDialog

class ShopDetailActivity : BaseBusinessActivity<ActivityShopDetailBinding, ShopDetailViewModel>(ShopDetailViewModel::class.java,BR.vm) {

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
                setPositiveButton(StringUtils.getString(R.string.delete)){
                    mViewModel.requestShopDelete()
                }
            }.build()
                .show(supportFragmentManager)
        }
    }

    override fun initEvent() {
        super.initEvent()
//        mSharedViewModel.hasShopAppointPermission.observe(this) {
//            mBinding.btnShopDetailAppointment.visibility = if (it) View.VISIBLE else View.GONE
//        }

        mSharedViewModel.hasShopUpdatePermission.observe(this) {
            mBinding.btnShopDetailEdit.visibility = if (it) View.VISIBLE else View.GONE
        }

        mSharedViewModel.hasShopDeletePermission.observe(this) {
            mBinding.btnShopDetailDelete.visibility = if (it) View.VISIBLE else View.GONE
        }

        // 刷新预约布局
        mViewModel.shopDetail.observe(this) {
            mBinding.bgShopDetailAppointmentInfo.visibility =
                if (it.appointSettingList.isNullOrEmpty()) View.GONE else View.VISIBLE
            mBinding.llShopDetailAppointmentInfo.buildChild<ItemShopDetailAppointmentBinding, AppointSetting>(
                it.appointSettingList
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