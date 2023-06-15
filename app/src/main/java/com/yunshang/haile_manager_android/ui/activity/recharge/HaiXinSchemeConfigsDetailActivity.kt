package com.yunshang.haile_manager_android.ui.activity.recharge

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.RewardsConfig
import com.yunshang.haile_manager_android.databinding.ActivityHaixinSchemeConfigsDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemShopDetailAppointmentBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.SharedBottomDialog
import com.yunshang.haile_manager_android.utils.FileUtils
import com.yunshang.haile_manager_android.utils.QrcodeUtils
import com.yunshang.haile_manager_android.utils.WeChatHelper

class HaiXinSchemeConfigsDetailActivity :
    BaseBusinessActivity<ActivityHaixinSchemeConfigsDetailBinding, HaiXinSchemeConfigsDetailViewModel>(
        HaiXinSchemeConfigsDetailViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_scheme_configs_detail

    override fun backBtn(): View = mBinding.barSchemeDetailTitle.getBackBtn()

    // 权限
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                showRechargeQrCode()
            } else {
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
                                data.reach!! * 1.0 / schemeDetail.exchangeRate,
                                data.reward!! * 1.0 / schemeDetail.exchangeRate
                            )
                        }"
                    }
                    childBinding.tvShopDetailsAppointmentValue.text =
                        StringUtils.getString(if (0 == data.status) R.string.no_configure else R.string.configured)
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
        mBinding.btnSchemeDetailRecharge.setOnClickListener {
            requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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