package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.BankCardDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityBankCardDetailBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.utils.GlideUtils

class BankCardDetailActivity :
    BaseBusinessActivity<ActivityBankCardDetailBinding, BankCardDetailViewModel>(
        BankCardDetailViewModel::class.java, BR.vm
    ) {

    // 跳转短信验证界面
    private val startSms =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { resultData ->
                    IntentParams.WithdrawBindAlipayParams.parseAuthCode(resultData)
                        ?.let { authCode ->
                            when (IntentParams.BindSmsVerifyParams.parseVerifyType(resultData)) {
                                3 -> {
                                    mViewModel.deleteBankCard(authCode) {
                                        SToast.showToast(
                                            this@BankCardDetailActivity,
                                            R.string.delete_success
                                        )
                                        finish()
                                    }
                                }
                                4 -> {
                                    mViewModel.releaseBankCard(authCode) {
                                        SToast.showToast(
                                            this@BankCardDetailActivity,
                                            R.string.unBind_success
                                        )
                                        finish()
                                    }
                                }
                                5 -> {
                                    mViewModel.bankCardDetail.value?.let { detail ->
                                        startActivity(
                                            Intent(
                                                this@BankCardDetailActivity,
                                                BankCardBindActivity::class.java
                                            ).apply {
                                                putExtras(
                                                    IntentParams.BinkCardBindParams.pack(
                                                        authCode,
                                                        detail
                                                    )
                                                )
                                            })
                                    }
                                }
                                else -> {}
                            }
                        }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_bank_card_detail

    override fun backBtn(): View = mBinding.barBankCardDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.cardId = IntentParams.CommonParams.parseId(intent)
    }

    override fun initEvent() {
        super.initEvent()

        // 监听刷新
        LiveDataBus.with(BusEvents.BANK_LIST_DETAIL_STATUS)?.observe(this) {
            mViewModel.requestData()
        }

        mViewModel.bankCardDetail.observe(this) { detail ->
            detail?.let {
                // 银行卡或许可证照片
                GlideUtils.glideDefaultFactory(
                    mBinding.ivBankCardDetailBankPic,
                    detail.bankCardPic
                ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                    .into(mBinding.ivBankCardDetailBankPic)

                // 门店招牌照
                GlideUtils.glideDefaultFactory(
                    mBinding.ivBankCardDetailShopSignPic,
                    detail.doorImage
                ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                    .into(mBinding.ivBankCardDetailShopSignPic)

                // 设备场景照
                GlideUtils.glideDefaultFactory(
                    mBinding.ivBankCardDetailShopDevicePic,
                    detail.deviceImage
                ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                    .into(mBinding.ivBankCardDetailShopDevicePic)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 删除
        mBinding.btnBankCardDelete.setOnClickListener {
            CommonDialog.Builder("确定删除该银行卡信息吗？").apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.sure)) {
                    startSms.launch(
                        Intent(
                            this@BankCardDetailActivity,
                            BindSmsVerifyActivity::class.java
                        ).apply {
                            putExtras(IntentParams.BindSmsVerifyParams.pack(3, true))
                        })
                }
            }.build().show(supportFragmentManager)
        }

        // 解绑
        mBinding.btnBankCardUnBind.setOnClickListener {
            CommonDialog.Builder("确定解绑该银行卡信息吗？").apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.sure)) {
                    startSms.launch(
                        Intent(
                            this@BankCardDetailActivity,
                            BindSmsVerifyActivity::class.java
                        ).apply {
                            putExtras(IntentParams.BindSmsVerifyParams.pack(4, true))
                        })
                }
            }.build().show(supportFragmentManager)
        }

        // 重新绑定
        mBinding.btnBankCardRebinding.setOnClickListener {
            startSms.launch(
                Intent(
                    this@BankCardDetailActivity,
                    BindSmsVerifyActivity::class.java
                ).apply {
                    putExtras(IntentParams.BindSmsVerifyParams.pack(5, true))
                })
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}