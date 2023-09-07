package com.yunshang.haile_manager_android.ui.fragment

import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindCardInfoViewModel
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.databinding.FragmentBankCardBindCardInfoBinding
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
    }

    private fun loadBankLicence() {
        mActivityViewModel.bankCardParams.value?.let {bankCard->
            mBinding.ivOpenBankLicence.let {imageView->
                mActivityViewModel.authInfo.value?.let {
                    if(3 == it.verifyType){
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