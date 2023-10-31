package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.lsy.framelib.BR
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopFlowOperationSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityShopFlowOperationSettingBinding
import com.yunshang.haile_manager_android.databinding.ActivityShopOperationSettingBinding
import com.yunshang.haile_manager_android.databinding.PopupShopOperationSettingPromtBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow

class ShopFlowOperationSettingActivity :
    BaseBusinessActivity<ActivityShopFlowOperationSettingBinding, ShopFlowOperationSettingViewModel>(
        ShopFlowOperationSettingViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_flow_operation_setting

    override fun backBtn(): View = mBinding.barOperationSettingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.openSetting1.value =
            1 == IntentParams.ShopOperationSettingParams.parseVolumeVisibleState(intent)

        mViewModel.shopId = IntentParams.ShopParams.parseShopId(intent)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnShopParamsSettingSave.setOnClickListener {
            val openVal = if (true == mViewModel.openSetting1.value) 1 else 0
            if (mViewModel.shopId > 0) {
                mViewModel.submit(openVal) {
                    SToast.showToast(this, R.string.update_success)
                    finish()
                }
            } else {
                setResult(IntentParams.ShopOperationSettingParams.ResultCode, Intent().apply {
                    putExtras(IntentParams.ShopOperationSettingParams.pack(openVal))
                })
                finish()
            }
        }
        mBinding.tvShopOperationSettingTitle1.setOnClickListener {
            showDeviceOperateView(it)
        }
    }

    private fun showDeviceOperateView(view: View) {
        val mPopupBinding =
            PopupShopOperationSettingPromtBinding.inflate(LayoutInflater.from(this))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this, 277f),
        )
        popupWindow.showAsDropDown(
            view,
            -DimensionUtils.dip2px(this, -34f),
            0
        )
    }

    override fun initData() {
    }
}