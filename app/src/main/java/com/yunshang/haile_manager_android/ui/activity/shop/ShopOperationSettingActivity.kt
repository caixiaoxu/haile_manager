package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopOperationSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityShopOperationSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class ShopOperationSettingActivity :
    BaseBusinessActivity<ActivityShopOperationSettingBinding, ShopOperationSettingViewModel>(
        ShopOperationSettingViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_operation_setting

    override fun backBtn(): View = mBinding.barOperationSettingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.openSetting1.value =
            1 == IntentParams.ShopOperationSettingParams.parseVolumeVisibleState(intent)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnShopParamsSettingSave.setOnClickListener {
            setResult(IntentParams.ShopOperationSettingParams.ResultCode, Intent().apply {
                putExtras(IntentParams.ShopOperationSettingParams.pack(if (true == mViewModel.openSetting1.value) 1 else 0))
            })
            finish()
        }
    }

    override fun initData() {
    }
}