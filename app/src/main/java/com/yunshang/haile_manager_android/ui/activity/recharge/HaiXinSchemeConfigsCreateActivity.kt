package com.yunshang.haile_manager_android.ui.activity.recharge

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinSchemeConfigsCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.RewardsConfig
import com.yunshang.haile_manager_android.databinding.ActivityHaixinSchemeConfigsCreateBinding
import com.yunshang.haile_manager_android.databinding.ItemSchemeConfigCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

class HaiXinSchemeConfigsCreateActivity :
    BaseBusinessActivity<ActivityHaixinSchemeConfigsCreateBinding, HaiXinSchemeConfigsCreateViewModel>(
        HaiXinSchemeConfigsCreateViewModel::class.java, BR.vm
    ) {
    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        if (selected.isNotEmpty()) {
                            mViewModel.selectShop.value = selected[0]
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_haixin_scheme_configs_create

    override fun backBtn(): View = mBinding.barSchemeCreateTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        IntentParams.ShopParams.parseShopId(intent).let { shopId ->
            if (-1 != shopId) {
                mViewModel.shopId = shopId
                mViewModel.createUpdateParams.value?.shopId = shopId
                mViewModel.selectShop.postValue(
                    SearchSelectParam(
                        shopId,
                        IntentParams.ShopParams.parseShopName(intent) ?: ""
                    )
                )
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        if (-1 != mViewModel.shopId) {
            mBinding.barSchemeCreateTitle.setTitle(R.string.update_scheme)
        }

        mViewModel.createUpdateParams.observe(this) {
            mBinding.llSchemeCreateList.buildChild<ItemSchemeConfigCreateBinding, RewardsConfig>(
                if (-1 == mViewModel.shopId) it.rewards else it.updateRewards,
            ) { index, childBinding, data ->
                childBinding.item = data
                childBinding.index = index + 1
                childBinding.rate = it.exchangeRate

                data.isOpen.observe(this) { isOpen ->
                    childBinding.groupSchemeConfig.visibility =
                        if (isOpen) View.VISIBLE else View.GONE
                }
                data.reachVal.observe(this) { reach ->
                    data.reach = try {
                        (if (reach.isNullOrEmpty()) 0 else (reach.toDouble() * it.exchangeRate).toInt()).apply {
                            childBinding.tvSchemeReachHaixin.text =
                                "${this}${StringUtils.getString(R.string.hai_xin)}"
                        }
                    } catch (e: Exception) {
                        0
                    }
                }
                data.reach?.let { reach ->
                    data.reachVal.value = ((reach * 1.0 / it.exchangeRate).toString())
                }
                data.rewardVal.observe(this) { reach ->
                    data.reward = try {
                        (if (reach.isNullOrEmpty()) 0 else (reach.toDouble() * it.exchangeRate).toInt())
                            .apply {
                                childBinding.tvSchemeRewardHaixin.text =
                                    "${this}${StringUtils.getString(R.string.hai_xin)}"
                            }
                    } catch (e: Exception) {
                        0
                    }
                }
                data.reward?.let { reward ->
                    data.rewardVal.value = ((reward * 1.0 / it.exchangeRate).toString())
                }
            }
        }

        mViewModel.selectShop.observe(this) {
            mViewModel.createUpdateParams.value?.shopId = it.id
            mViewModel.requestSchemeDetail()
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        (-1 != mViewModel.shopId).let { isUpdate ->
            mBinding.itemSchemeName.contentView.isEnabled = !isUpdate
            mBinding.itemSchemeShop.contentView.isEnabled = !isUpdate
        }

        mBinding.itemSchemeShop.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@HaiXinSchemeConfigsCreateActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(IntentParams.SearchSelectTypeParam.pack(IntentParams.SearchSelectTypeParam.SearchSelectTypeRechargeShop))
                })
        }
    }

    override fun initData() {
        mViewModel.requestSchemeDetail()
    }
}