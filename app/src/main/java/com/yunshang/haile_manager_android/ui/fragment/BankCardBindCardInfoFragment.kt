package com.yunshang.haile_manager_android.ui.fragment

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindCardInfoViewModel
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
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
                mActivityViewModel.bankCardParams.value?.areaVal =
                    province.areaName + city.areaName + distract.areaName
            }
        }.build()
    }

    // 跳转银行和支行
    private val startBankAndSubNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {

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

        // 身份证正面
        mBinding.ivOpenBankLicence.setOnClickListener {
            DialogUtils.showImgSelectorDialog(requireActivity(), 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mActivityViewModel.uploadBankPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
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
                mActivityViewModel.authInfo.value?.let {
                    if (2 == it.verifyType) {
                        GlideUtils.glideDefaultFactory(
                            imageView,
                            bankCard.licenceForOpeningAccountImage,
                            R.mipmap.icon_open_bank_licence
                        ).into(imageView)
                    } else {
                        GlideUtils.glideDefaultFactory(
                            imageView,
                            bankCard.bankCardImage,
                            R.mipmap.icon_open_bank_card_pic
                        ).into(imageView)
                    }
                }
            }
        }
    }

    override fun initData() {
    }
}