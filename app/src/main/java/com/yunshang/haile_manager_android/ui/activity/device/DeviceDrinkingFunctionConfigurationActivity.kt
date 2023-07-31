package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceDrinkingFunctionConfigurationViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.databinding.ActivityDeviceDrinkingFunctionConfigurationBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceDrinkingFunctionConfigurationActivity :
    BaseBusinessActivity<ActivityDeviceDrinkingFunctionConfigurationBinding, DeviceDrinkingFunctionConfigurationViewModel>(
        DeviceDrinkingFunctionConfigurationViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_drinking_function_configuration

    override fun backBtn(): View = mBinding.barDeviceDrinkingFuncConfigurationTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodsId = IntentParams.DeviceFunctionConfigurationParams.parseGoodId(intent)
        mViewModel.spuId = IntentParams.DeviceFunctionConfigurationParams.parseSpuId(intent)
        mViewModel.categoryCode = IntentParams.DeviceParams.parseCategoryCode(intent)
        mViewModel.oldConfigurationList =
            IntentParams.DeviceFunctionConfigurationParams.parseOldFuncConfiguration(intent)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemDeviceFunConfigurationCalculateModel.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.pricing_manner), listOf(
                    SearchSelectParam(1, getString(R.string.for_time)),
                    SearchSelectParam(2, getString(R.string.for_quantity)),
                )
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.drinkAttrConfigure.value?.priceCalculateMode = data.id
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.resultData.observe(this) {
            setResult(IntentParams.DeviceFunctionConfigurationParams.ResultCode, Intent().apply {
                putExtras(IntentParams.DeviceFunctionConfigurationParams.packResult(it))
            })
            finish()
        }

        mViewModel.jump.observe(this) {
            when (it) {
                0 -> finish()
            }
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}