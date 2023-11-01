package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopPayBatchSettingsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.GoodsSetting
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.databinding.ActivityShopBatchPaySettingsBinding
import com.yunshang.haile_manager_android.databinding.ItemShopPaySettingsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

class ShopBatchPaySettingsActivity :
    BaseBusinessActivity<ActivityShopBatchPaySettingsBinding, ShopPayBatchSettingsViewModel>(
        ShopPayBatchSettingsViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                mViewModel.selectShops.value =
                                    GsonUtils.json2List(json, SearchSelectParam::class.java)
                            }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_shop_batch_pay_settings

    override fun backBtn(): View = mBinding.barShopPaySettingsTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.selectShops.observe(this){
            mViewModel.requestData()
        }

        mViewModel.shopPaySettings.observe(this) {
            it?.let {
                mBinding.llShopPaySettings.buildChild<ItemShopPaySettingsBinding, GoodsSetting>(
                    it.goodsSettingList,
                    LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DimensionUtils.dip2px(this@ShopBatchPaySettingsActivity, 54f)
                    ), 1
                ) { _, childBinding, data ->
                    childBinding.item = data

                    childBinding.switchShopPaySettingsNoPwdPayOpen.setOnSwitchClickListener {
                        SToast.showToast(this@ShopBatchPaySettingsActivity, "免密支付功能，暂不可使用")
                        true
                    }
                    childBinding.switchShopPaySettingsCompelNoPwdPayOpen.setOnSwitchClickListener {
                        SToast.showToast(this@ShopBatchPaySettingsActivity, "免密支付功能，暂不可使用")
                        true
                    }
                }
            }
        }

        mViewModel.jump.observe(this){
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.llShopBatchPaySettingShops.setOnClickListener {
            startSearchSelect.launch(Intent(
                this@ShopBatchPaySettingsActivity,
                SearchSelectRadioActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.SearchSelectTypeParam.pack(
                        IntentParams.SearchSelectTypeParam.SearchSelectTypePaySettingsShop,
                        mustSelect = true,
                        moreSelect = true,
                        selectArr = mViewModel.selectShops.value?.map { item -> item.id }
                            ?.toIntArray() ?: intArrayOf()
                    )
                )
            })
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}