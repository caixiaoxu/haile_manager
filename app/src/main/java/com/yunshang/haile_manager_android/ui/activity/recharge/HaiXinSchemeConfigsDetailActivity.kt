package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.FileUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.SystemPermissionHelper
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.RewardsConfig
import com.yunshang.haile_manager_android.data.extend.formatMoney
import com.yunshang.haile_manager_android.databinding.ActivityHaixinSchemeConfigsDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemShopDetailAppointmentBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.dialog.SharedBottomDialog
import com.yunshang.haile_manager_android.utils.QrcodeUtils
import com.yunshang.haile_manager_android.utils.WeChatHelper

class HaiXinSchemeConfigsDetailActivity :
    BaseBusinessActivity<ActivityHaixinSchemeConfigsDetailBinding, HaiXinSchemeConfigsDetailViewModel>(
        HaiXinSchemeConfigsDetailViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_scheme_configs_detail

    override fun backBtn(): View = mBinding.barSchemeDetailTitle.getBackBtn()

    // 权限
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.any { it }) {
                showRechargeQrCode()
            } else {
                // 授权失败
                SToast.showToast(this, R.string.empty_permission)
            }
        }

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopId = IntentParams.ShopParams.parseShopId(intent)
        mViewModel.shopName = IntentParams.ShopParams.parseShopName(intent) ?: ""
    }


    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.HAIXIN_SCHEME_DETAIL_STATUS)?.observe(this) {
            mViewModel.requestSchemeDetailAsync()
        }

        mViewModel.schemeDetail.observe(this) {
            it?.let { schemeDetail ->

                // 刷新配置布局
                mBinding.llSchemeDetailInfo.visibility =
                    if (it.rewardsConfig.isEmpty()) View.GONE else View.VISIBLE
                mBinding.llSchemeDetailInfo.buildChild<ItemShopDetailAppointmentBinding, RewardsConfig>(
                    it.rewardsConfig
                ) { index, childBinding, data ->
                    if (null != data.reach && null != data.reward) {
                        childBinding.tvShopDetailsAppointmentName.text = "${
                            StringUtils.getString(
                                R.string.scheme_config_index,
                                index + 1,
                            )
                        }：${
                            StringUtils.getString(
                                R.string.scheme_config_title,
                                (data.reach!! * 1.0 / schemeDetail.exchangeRate).formatMoney(),
                                (data.reward!! * 1.0 / schemeDetail.exchangeRate).formatMoney()
                            )
                        }"
                    }
                    childBinding.tvShopDetailsAppointmentValue.text =
                        StringUtils.getString(if (0 == data.status) R.string.configured else R.string.no_configure)
                    childBinding.tvShopDetailsAppointmentValue.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            if (0 == data.status) R.color.common_sub_txt_color else R.color.colorPrimary,
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.switchSchemeDetailOpen.setOnSwitchClickListener() {
            mViewModel.switchSchemeOpen()
            false
        }

        mBinding.btnSchemeDetailRecharge.setOnClickListener {
            requestPermissions.launch(SystemPermissionHelper.readWritePermissions())
        }
        mBinding.btnSchemeDetailEdit.setOnClickListener {
            startActivity(
                Intent(
                    this@HaiXinSchemeConfigsDetailActivity,
                    HaiXinSchemeConfigsCreateActivity::class.java
                ).apply {
                    mViewModel.schemeDetail.value?.let { detail ->
                        putExtras(IntentParams.ShopParams.pack(detail.shopId, mViewModel.shopName))
                    }
                }
            )
        }
        mBinding.btnSchemeDetailDelete.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.scheme_config_delete_hint)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.delete)) {
                    mViewModel.deleteScheme()
                }
            }.build()
                .show(supportFragmentManager)
        }
    }

    private fun showRechargeQrCode() {
        SharedBottomDialog() {
            mViewModel.rechargeQrCode.value?.let { qrCode ->
                val bitmap: Bitmap = QrcodeUtils.createQRCodeBitmap(
                    qrCode,
                    300, 300, "UTF-8", "M", "0", Color.parseColor("#000000"),
                    Color.parseColor("#FFFFFF")
                )
                when (it) {
                    SharedBottomDialog.SHARED_ALBUM ->                         // 保存到相册
                        try {
                            FileUtils.saveImageToGallery(
                                this@HaiXinSchemeConfigsDetailActivity,
                                bitmap
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    SharedBottomDialog.SHARED_WX -> {
                        if (!WeChatHelper.isWxInstall) {
                            SToast.showToast(
                                this@HaiXinSchemeConfigsDetailActivity,
                                R.string.err_not_install_wechat
                            )
                            return@SharedBottomDialog
                        }
                        // 分享到微信
                        WeChatHelper.openWeChatImgShare(bitmap)
                    }
                }
            }
        }.show(supportFragmentManager, "shared_dialog")
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestData()
    }

}