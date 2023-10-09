package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ShopPositionCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity
import com.yunshang.haile_manager_android.data.arguments.BusinessHourParams
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityShopPositionCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class ShopPositionCreateActivity :
    BaseBusinessActivity<ActivityShopPositionCreateBinding, ShopPositionCreateViewModel>(
        ShopPositionCreateViewModel::class.java, BR.vm
    ) {


    // 搜索界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                IntentParams.ShopBusinessHoursParams.ResultCode -> {
                    it.data?.let { intent ->
                        IntentParams.ShopBusinessHoursParams.parseShopBusinessHoursJson(intent)
                            ?.let { hours ->
                                mViewModel.positionParam.value?.changeWorkTime(
                                    GsonUtils.json2List(
                                        hours,
                                        BusinessHourEntity::class.java
                                    )
                                )
                            }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_shop_position_create

    override fun backBtn(): View = mBinding.barAddPtTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 营业时间
        mBinding.itemPositionBusinessHours.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@ShopPositionCreateActivity,
                    ShopBusinessHoursActivity::class.java
                ).apply {
                    GsonUtils.json2List(
                        mViewModel.positionParam.value?.workTimeStr,
                        BusinessHourParams::class.java
                    )?.let { params ->
                        putExtras(IntentParams.ShopBusinessHoursParams.pack(params.map {
                            BusinessHourEntity().apply {
                                formatData(it.weekDays, it.workTime)
                            }
                        }.toMutableList()))
                    }
                })
        }
    }

    override fun initData() {
    }
}