package com.yunshang.haile_manager_android.ui.activity.recharge

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.RewardsConfig
import com.yunshang.haile_manager_android.data.rule.IncomeDetailInfo
import com.yunshang.haile_manager_android.databinding.ActivityHaixinSchemeConfigsCreateBinding
import com.yunshang.haile_manager_android.databinding.ItemIncomeDetailInfoBinding
import com.yunshang.haile_manager_android.databinding.ItemSchemeConfigCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class HaiXinSchemeConfigsCreateActivity :
    BaseBusinessActivity<ActivityHaixinSchemeConfigsCreateBinding, HaiXinSchemeConfigsCreateViewModel>(
        HaiXinSchemeConfigsCreateViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_haixin_scheme_configs_create

    override fun backBtn(): View = mBinding.barSchemeCreateTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopId = IntentParams.ShopParams.parseShopId(intent)
        mViewModel.shopName.postValue(IntentParams.ShopParams.parseShopName(intent) ?: "")
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.createUpdateParams.observe(this){
            mBinding.llSchemeCreateParent.buildChild<ItemSchemeConfigCreateBinding, RewardsConfig>(
                it.rewards
            ) { _, childBinding, data ->
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        (-1 != mViewModel.shopId).let { isUpdate ->
            mBinding.itemSchemeName.contentView.isEnabled = !isUpdate
            mBinding.itemSchemeShop.contentView.isEnabled = !isUpdate
        }
    }

    override fun initData() {
        mViewModel.requestSchemeDetail()
    }
}