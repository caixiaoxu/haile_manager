package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.entities.ShopBatchSettingViewModel
import com.yunshang.haile_manager_android.databinding.ActivityShopBatchSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class ShopBatchSettingActivity :
    BaseBusinessActivity<ActivityShopBatchSettingBinding, ShopBatchSettingViewModel>(
        ShopBatchSettingViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_batch_setting

    override fun backBtn(): View = mBinding.barShopBatchSettingTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.shared = mSharedViewModel
        // 批量支付设置
        mBinding.clShopBatchSettingPaySetting.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopBatchSettingActivity,
                    ShopBatchPaySettingsActivity::class.java
                )
            )
        }
        // 批量补偿设置
        mBinding.clShopBatchSettingCompensationSetting.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopBatchSettingActivity,
                    ShopBatchCompensationSettingActivity::class.java
                )
            )
        }
        // 批量预约设置
        mBinding.clShopBatchSettingAppointmentSetting.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopBatchSettingActivity,
                    ShopAppointmentSettingActivity::class.java
                )
            )
        }
        // 批量流量设置
        mBinding.clShopBatchSettingFlowSetting.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopBatchSettingActivity,
                    ShopFlowOperationSettingActivity::class.java
                )
            )
        }
    }

    override fun initData() {
    }
}