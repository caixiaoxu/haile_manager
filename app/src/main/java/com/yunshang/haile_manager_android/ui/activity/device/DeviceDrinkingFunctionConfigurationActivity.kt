package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceDrinkingFunctionConfigurationViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DrinkAttrConfigure
import com.yunshang.haile_manager_android.databinding.ActivityDeviceDrinkingFunctionConfigurationBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDrinkingFunctionConfigurationBinding
import com.yunshang.haile_manager_android.databinding.PopupDeviceConfigureTipBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow
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


    override fun initEvent() {
        super.initEvent()

        mViewModel.drinkAttrConfigure.observe(this) {
            it?.let {
                mBinding.llDeviceFunConfigurationItems.buildChild<ItemDeviceDrinkingFunctionConfigurationBinding, DrinkAttrConfigure.DrinkAttrConfigureItem>(
                    it.items,
                ) { _, childBinding, data ->
                    childBinding.item = data
                    it.priceCalculateMode.observe(this) { model ->
                        childBinding.model = model
                    }
                    childBinding.switchDeviceDrinkingFunConfigurationNormalOpen.setOnSwitchClickListener {switch->
                        if (switch.isChecked) {
                            //如果当前是开启状态，关闭前先判断是否只开启了一个
                            val openNum =
                                mViewModel.drinkAttrConfigure.value?.items?.count { item -> 1 == item.soldState }
                                    ?: 0
                            if (1 >= openNum) {
                                SToast.showToast(msg = "请至少开启一个单价")
                                true
                            } else false
                        } else false

                    }
                }
            }
        }

        mViewModel.resultData.observe(this)
        {
            setResult(IntentParams.DeviceFunctionConfigurationParams.ResultCode, Intent().apply {
                putExtras(IntentParams.DeviceFunctionConfigurationParams.packResult(it))
            })
            finish()
        }

        mViewModel.jump.observe(this)
        {
            when (it) {
                0 -> finish()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemDeviceFunConfigurationCalculateModel.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.pricing_manner), listOf(
                    SearchSelectParam(1, getString(R.string.for_quantity)),
                    SearchSelectParam(2, getString(R.string.for_time)),
                )
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.drinkAttrConfigure.value?.priceCalculateMode?.value = data.id
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        mBinding.itemDeviceFunConfigurationOvertime.mTitleView.setOnClickListener {
            showDeviceConfigureTipView(it, "持续放水最长时间")
        }

        mBinding.itemDeviceFunConfigurationPauseTime.mTitleView.setOnClickListener {
            showDeviceConfigureTipView(it, "暂停超过时间后自动停止")
        }
    }

    /**
     * 显示配置提示界面
     */
    private fun showDeviceConfigureTipView(view: View, tip: String) {
        val mPopupBinding =
            PopupDeviceConfigureTipBinding.inflate(LayoutInflater.from(this@DeviceDrinkingFunctionConfigurationActivity))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this@DeviceDrinkingFunctionConfigurationActivity, 136f),
            alpha = 1f
        )

        mPopupBinding.tvDeviceConfigureTip.text = tip
        popupWindow.showAsAbove(
            view,
            view.width - DimensionUtils.dip2px(
                this@DeviceDrinkingFunctionConfigurationActivity,
                33f
            ),
        )
    }

    override fun initData() {
        mViewModel.requestData()
    }
}