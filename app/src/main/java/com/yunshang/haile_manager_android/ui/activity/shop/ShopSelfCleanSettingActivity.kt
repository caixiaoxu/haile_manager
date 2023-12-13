package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.BR
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopSelfCleanSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.databinding.ActivityShopSelfCleanSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

class ShopSelfCleanSettingActivity :
    BaseBusinessActivity<ActivityShopSelfCleanSettingBinding, ShopSelfCleanSettingViewModel>(
        ShopSelfCleanSettingViewModel::class.java, BR.vm
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

    override fun layoutId(): Int = R.layout.activity_shop_self_clean_setting

    override fun backBtn(): View = mBinding.barOperationSettingTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this){
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.llShopBatchFlowSettingShops.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@ShopSelfCleanSettingActivity,
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
    }
}