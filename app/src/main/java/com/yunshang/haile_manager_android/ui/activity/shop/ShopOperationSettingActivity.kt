package com.yunshang.haile_manager_android.ui.activity.shop

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.BR
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopOperationSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.GoodsSetting
import com.yunshang.haile_manager_android.data.entities.SettingItem
import com.yunshang.haile_manager_android.databinding.ActivityShopOperationSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemShopAppointmentSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPaySettingsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class ShopOperationSettingActivity :
    BaseBusinessActivity<ActivityShopOperationSettingBinding, ShopOperationSettingViewModel>(
        ShopOperationSettingViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemShopAppointmentSettingBinding, SettingItem>(
            R.layout.item_shop_appointment_setting,
            com.yunshang.haile_manager_android.BR.item
        ) { _, _, _ ->
        }
    }

    override fun layoutId(): Int = R.layout.activity_shop_operation_setting

    override fun backBtn(): View = mBinding.barOperationSettingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.shopId = IntentParams.ShopParams.parseShopId(intent)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.operationSettingDetail.observe(this) { settings ->
            // 支付设置
            mBinding.llShopPaySettings.buildChild<ItemShopPaySettingsBinding, GoodsSetting>(
                settings?.paymentSetting?.goodsSettingList,
                LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    DimensionUtils.dip2px(this@ShopOperationSettingActivity, 54f)
                ), 1
            ) { _, childBinding, data ->
                childBinding.item = data
                childBinding.switchShopPaySettingsNoPwdPayOpen.setOnSwitchClickListener {
                    SToast.showToast(this@ShopOperationSettingActivity, "免密支付功能，暂不可使用")
                    true
                }
                childBinding.switchShopPaySettingsCompelNoPwdPayOpen.setOnSwitchClickListener {
                    SToast.showToast(this@ShopOperationSettingActivity, "免密支付功能，暂不可使用")
                    true
                }
            }

            // 预约设置
            mAdapter.refreshList(settings?.appointSetting?.settingList, true)
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.shared = mSharedViewModel

        mBinding.rvShopAppointmentOperationSettingList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopAppointmentOperationSettingList.adapter = mAdapter

        mBinding.tvShopFlowOperationSettingTitle.setOnClickListener {
            AlertDialog.Builder(this@ShopOperationSettingActivity)
                .setView(R.layout.popup_shop_operation_setting_promt).create().show()
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}