package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindCardInfoViewModel
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.BankEntity
import com.yunshang.haile_manager_android.data.entities.SubBankEntity
import com.yunshang.haile_manager_android.databinding.FragmentBankCardBindCardInfoBinding
import com.yunshang.haile_manager_android.ui.activity.common.SearchLetterActivity
import com.yunshang.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.yunshang.haile_manager_android.utils.DialogUtils
import com.yunshang.haile_manager_android.utils.GlideUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/6 13:59
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BankCardBindCardInfoFragment :
    BaseBusinessFragment<FragmentBankCardBindCardInfoBinding, BankCardBindCardInfoViewModel>(
        BankCardBindCardInfoViewModel::class.java
    ) {
    private val mActivityViewModel by lazy {
        getActivityViewModelProvider(requireActivity())[BankCardBindViewModel::class.java]
    }

    // 区域选择
    private val mAreaDialog: AreaSelectDialog by lazy {
        AreaSelectDialog.Builder().apply {
            onAreaSelect = { province, city, distract ->
                mActivityViewModel.bankCardParams.value?.bankProvinceId = province.areaId
                mActivityViewModel.bankCardParams.value?.bankCityId = city.areaId
                mActivityViewModel.bankCardParams.value?.bankDistrictId = distract.areaId
                mActivityViewModel.bankCardParams.value?.bankAreaVal =
                    province.areaName + city.areaName + distract.areaName
            }
        }.build()
    }

    // 跳转银行和支行
    private val startBankAndSubNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                it.data?.let { intent ->
                    val type = IntentParams.SearchLetterParams.parseSearchLetterType(intent)
                    if (0 == type) {
                        GsonUtils.json2Class(
                            IntentParams.SearchLetterParams.parseResultData(intent),
                            BankEntity::class.java
                        )?.let { bank ->
                            mActivityViewModel.bankCardParams.value?.bankCodeVal = bank.bankCode
                            mActivityViewModel.bankCardParams.value?.bankNameVal = bank.bankName
                        }
                    } else if (1 == type) {
                        GsonUtils.json2Class(
                            IntentParams.SearchLetterParams.parseResultData(intent),
                            SubBankEntity::class.java
                        )?.let { subBank ->
                            mActivityViewModel.bankCardParams.value?.subBankCodeVal =
                                subBank.subBankCode
                            mActivityViewModel.bankCardParams.value?.subBankNameVal =
                                subBank.subBankName
                        }
                    } else {

                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.fragment_bank_card_bind_card_info

    override fun initEvent() {
        super.initEvent()
        mActivityViewModel.bankCardParams.observe(this) {
            it?.let {
                loadBankLicence()
            }
        }
    }

    override fun initView() {
        mBinding.parentVm = mActivityViewModel

        // 地区
        mBinding.itemBankCardBindCardOpenBankArea.onSelectedEvent = {
            mAreaDialog.show(childFragmentManager)
        }
        // 开户银行
        mBinding.itemBankCardBindCardOpenBank.onSelectedEvent = {
            startBankAndSubNext.launch(Intent(
                requireContext(), SearchLetterActivity::class.java
            ).apply {
                putExtras(IntentParams.SearchLetterParams.pack(0))
            })
        }
        // 开户支行
        mBinding.itemBankCardBindCardOpenBankSubBranch.onSelectedEvent = {
            startBankAndSubNext.launch(Intent(
                requireContext(), SearchLetterActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.SearchLetterParams.pack(
                        1,
                        mActivityViewModel.bankCardParams.value?.bankCode
                    )
                )
            })
        }
        // 开户支行联行号
        mBinding.itemBankCardBindCardBankSubBranchLinesNo.onSelectedEvent = {
            startBankAndSubNext.launch(Intent(
                requireContext(), SearchLetterActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.SearchLetterParams.pack(
                        1,
                        mActivityViewModel.bankCardParams.value?.bankCode
                    )
                )
            })
        }

        // 身份证正面
        mBinding.ivOpenBankLicence.setOnClickListener {
            DialogUtils.showImgSelectorDialog(requireActivity(), 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mActivityViewModel.uploadBankPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            if (3 == mActivityViewModel.merchantType) {
                                mActivityViewModel.bankCardParams.value?.licenceForOpeningAccountImage =
                                    url
                            } else {
                                mActivityViewModel.bankCardParams.value?.bankCardImage = url
                            }
                            loadBankLicence()
                        } else SToast.showToast(requireActivity(), R.string.upload_failure)
                    }
                }
            }
        }
    }

    private fun loadBankLicence() {
        mActivityViewModel.bankCardParams.value?.let { bankCard ->
            mBinding.ivOpenBankLicence.let { imageView ->
                if (3 == mActivityViewModel.merchantType) {
                    GlideUtils.glideDefaultFactory(
                        imageView,
                        bankCard.licenceForOpeningAccountImage,
                        R.mipmap.icon_open_bank_licence
                    ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(requireContext(), 8f)))
                        .into(imageView)
                } else {
                    GlideUtils.glideDefaultFactory(
                        imageView,
                        bankCard.bankCardImage,
                        R.mipmap.icon_open_bank_card_pic
                    ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(requireContext(), 8f)))
                        .into(imageView)
                }
            }
        }
    }

    override fun initData() {
    }
}