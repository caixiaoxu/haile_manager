package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopPaySettingsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.GoodsSetting
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.databinding.ActivityShopPaySettingsBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPaySettingsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class ShopPaySettingsActivity :
    BaseBusinessActivity<ActivityShopPaySettingsBinding, ShopPaySettingsViewModel>(
        ShopPaySettingsViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_pay_settings

    override fun backBtn(): View = mBinding.barShopPaySettingsTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.shopId = IntentParams.ShopPaySettingsParams.parseShopId(intent)
        mViewModel.shopIds = IntentParams.ShopPaySettingsParams.parseShopIds(intent)
        mViewModel.oldShopPaySettings =
            IntentParams.ShopPaySettingsParams.parseShopPaySettings(intent)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.shopPaySettings.observe(this) {
            it?.let {
                mBinding.llShopPaySettings.buildChild<ItemShopPaySettingsBinding, GoodsSetting>(
                    it.goodsSettingList,
                    LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DimensionUtils.dip2px(this@ShopPaySettingsActivity, 54f)
                    ), 1
                ) { _, childBinding, data ->
                    childBinding.item = data

                    childBinding.switchShopPaySettingsNoPwdPayOpen.setOnSwitchClickListener {
                        SToast.showToast(this@ShopPaySettingsActivity, "免密支付功能，暂不可使用")
                        true
                    }
                    childBinding.switchShopPaySettingsCompelNoPwdPayOpen.setOnSwitchClickListener {
                        SToast.showToast(this@ShopPaySettingsActivity, "免密支付功能，暂不可使用")
                        true
                    }
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnShopPaySettingsSave.setOnClickListener {
            if (mViewModel.shopId.hasVal()) {
                mViewModel.updatePaySettings {
                    SToast.showToast(this@ShopPaySettingsActivity, R.string.update_success)
                    finish()
                }
            } else if (null == mViewModel.shopIds) {
                setResult(IntentParams.ShopPaySettingsParams.ResultCode, Intent().apply {
                    mViewModel.shopPaySettings.value?.let { settings ->
                        putExtras(IntentParams.ShopPaySettingsParams.pack(shopPaySettings = settings))
                    }
                })
                finish()
            } else {
                mViewModel.submitPaySettings {
                    SToast.showToast(this@ShopPaySettingsActivity, R.string.bind_success)
                    finish()
                }
            }
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}