package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.View
import com.google.gson.reflect.TypeToken
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceOtherParamsUpdateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityDeviceOtherParamsUpdateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceOtherParamsUpdateActivity :
    BaseBusinessActivity<ActivityDeviceOtherParamsUpdateBinding, DeviceOtherParamsUpdateViewModel>(
        DeviceOtherParamsUpdateViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_other_params_update

    override fun backBtn(): View = mBinding.barDeviceOtherParamsUpdateTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.updateParams = GsonUtils.json2ClassType<HashMap<String, Any?>>(
            IntentParams.DeviceParamsUpdateParams.parseUpdateParamsJson(intent),
            object : TypeToken<HashMap<String, Any>>() {}.type
        )

        // 初始化单脉冲流量
        mViewModel.singlePulseQuantityVal.value =
            ((mViewModel.attrList(mViewModel.skuList()?.firstOrNull() as? Map<*, *>)
                ?.firstOrNull() as? Map<*, *>)?.get("pulseVolumeFactor") as? String?) ?: ""
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this){
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}