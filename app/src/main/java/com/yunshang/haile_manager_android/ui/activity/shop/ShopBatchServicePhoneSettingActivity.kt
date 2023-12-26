package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopBatchServicePhoneSettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.databinding.ActivityShopBatchServicePhoneSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemShopSettingServicePhoneBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity

class ShopBatchServicePhoneSettingActivity :
    BaseBusinessActivity<ActivityShopBatchServicePhoneSettingBinding, ShopBatchServicePhoneSettingViewModel>(
        ShopBatchServicePhoneSettingViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        mViewModel.selectPositions.value =
                            IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_shop_batch_service_phone_setting

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }

        mViewModel.servicePhoneList.observe(this) {
            mBinding.llShopBatchServicePhoneSetting.buildChild<ItemShopSettingServicePhoneBinding, ShopBatchServicePhoneSettingViewModel.ServicePhoneEntity>(
                it, start = 1
            ) { index, childBinding, data ->
                childBinding.servicePhone = data

                mViewModel.servicePhoneList.observe(this) { list ->
                    childBinding.total = list.size
                }

                childBinding.ibShopSettingServicePhoneAddDel.setOnClickListener {
                    mViewModel.servicePhoneList.value =
                        if (data.isFirst) {
                            mViewModel.servicePhoneList.value?.also { list ->
                                list.add(
                                    ShopBatchServicePhoneSettingViewModel.ServicePhoneEntity(false)
                                )
                            }
                        } else {
                            mViewModel.servicePhoneList.value?.also { list ->
                                list.removeAt(index)
                            }
                        }
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.llShopBatchServicePhoneSettingShops.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@ShopBatchServicePhoneSettingActivity,
                    ShopPositionSelectorActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopPositionSelectorParams.pack(
                            mustSelect = false,
                            selectList = mViewModel.selectPositions.value,
                        )
                    )
                }
            )
        }
    }

    override fun initData() {
    }
}