package com.yunshang.haile_manager_android.ui.fragment

import com.lsy.framelib.ui.base.fragment.BaseBindingFragment
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindShopInfoViewModel
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.databinding.FragmentBankCardBindCardInfoBinding
import com.yunshang.haile_manager_android.databinding.FragmentBankCardBindShopInfoBinding

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

    override fun layoutId(): Int = R.layout.fragment_bank_card_bind_shop_info

    override fun initView() {
        mBinding.parentVm = mActivityViewModel
    }

    override fun initData() {
    }
}