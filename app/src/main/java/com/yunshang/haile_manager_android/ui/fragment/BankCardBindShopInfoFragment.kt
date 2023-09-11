package com.yunshang.haile_manager_android.ui.fragment

import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindShopInfoViewModel
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.databinding.FragmentBankCardBindShopInfoBinding
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
class BankCardBindShopInfoFragment :
    BaseBusinessFragment<FragmentBankCardBindShopInfoBinding, BankCardBindShopInfoViewModel>(
        BankCardBindShopInfoViewModel::class.java
    ) {
    private val mActivityViewModel by lazy {
        getActivityViewModelProvider(requireActivity())[BankCardBindViewModel::class.java]
    }

    // 区域选择
    private val mAreaDialog: AreaSelectDialog by lazy {
        AreaSelectDialog.Builder().apply {
            onAreaSelect = { province, city, distract ->
                mActivityViewModel.bankCardParams.value?.provinceId = province.areaId
                mActivityViewModel.bankCardParams.value?.cityId = city.areaId
                mActivityViewModel.bankCardParams.value?.districtId = distract.areaId
                mActivityViewModel.bankCardParams.value?.areaVal =
                    province.areaName + city.areaName + distract.areaName
            }
        }.build()
    }

    override fun layoutId(): Int = R.layout.fragment_bank_card_bind_shop_info

    override fun initView() {
        mBinding.parentVm = mActivityViewModel

        mBinding.itemBankCardBindShopInfoArea.onSelectedEvent = {
            mAreaDialog.show(childFragmentManager)
        }

        // 店铺招牌照片
        mBinding.ivBankCardShopInfoSign.setOnClickListener {
            DialogUtils.showImgSelectorDialog(requireActivity(), 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mActivityViewModel.uploadBankPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mActivityViewModel.bankCardParams.value?.doorImage = url
                            GlideUtils.glideDefaultFactory(
                                mBinding.ivBankCardShopInfoSign,
                                url,
                                R.mipmap.icon_open_bank_licence
                            ).into(mBinding.ivBankCardShopInfoSign)
                        } else SToast.showToast(requireActivity(), R.string.upload_failure)
                    }
                }
            }
        }

        // 店铺设备场景照片
        mBinding.ivBankCardShopInfoDevice.setOnClickListener {
            DialogUtils.showImgSelectorDialog(requireActivity(), 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mActivityViewModel.uploadBankPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mActivityViewModel.bankCardParams.value?.deviceImage = url
                            GlideUtils.glideDefaultFactory(
                                mBinding.ivBankCardShopInfoDevice,
                                url,
                                R.mipmap.icon_open_bank_licence
                            ).into(mBinding.ivBankCardShopInfoDevice)
                        } else SToast.showToast(requireActivity(), R.string.upload_failure)
                    }
                }
            }
        }
    }

    override fun initData() {
    }
}