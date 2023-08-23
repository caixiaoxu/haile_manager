package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.common.DeviceCategory.Dispenser
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.ExtAttrDrinkBean
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.databinding.ActivityDeviceCreateBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDetailDisposeMinBinding
import com.yunshang.haile_manager_android.databinding.ItemSelectedDeviceFuncationConfigurationBinding
import com.yunshang.haile_manager_android.databinding.ItemSelectedDrinkDeviceFuncationConfigurationBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber

class DeviceCreateActivity :
    BaseBusinessActivity<ActivityDeviceCreateBinding, DeviceCreateViewModel>(
        DeviceCreateViewModel::class.java,
        BR.vm
    ) {
    private var isAttrImei = false

    // 权限
    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
            if (result.values.any { it }) {
                // 授权权限成功
                startQRActivity(false)
            } else {
                // 授权失败
                SToast.showToast(this, R.string.empty_permission)
            }
        }

    private fun startQRActivity(isOne: Boolean) {
        startQRCodeScan.launch(Intent(
            this,
            WeChatQRCodeScanActivity::class.java
        ).apply {
            putExtra("isOne", isOne)
        })
    }

    // 二维码
    private val startQRCodeScan =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 扫码结果
            if (result.resultCode == RESULT_OK) {
                CameraScan.parseScanResult(result.data)?.let {
                    Timber.i("扫码:$it")
                    if (isAttrImei) {
                        Timber.i("IMEI:$it")
                        if (StringUtils.isImeiCode(it)) mViewModel.washimeiCode.value = it
                        else SToast.showToast(this, R.string.imei_code_error1)
                    } else {
                        StringUtils.getPayImeiCode(it)?.let { code ->
                            mViewModel.payCode.value = code
                            mViewModel.imeiCode.value = code
                            mViewModel.createAndUpdateEntity.value?.codeStr = it
                        } ?: run {
                            val payCode = StringUtils.getPayCode(it)
                            if (!mViewModel.isIgnorePayCodeFlag && null != payCode) {
                                mViewModel.payCode.value = payCode
                                mViewModel.createAndUpdateEntity.value?.codeStr = it
                            } else if (StringUtils.isImeiCode(it)) {
                                mViewModel.imeiCode.value = it
                                mBinding.mtivDeviceCreateImei.clearFocus()
                            } else
                                SToast.showToast(this, R.string.scan_code_error)
                        }
                    }
                } ?: SToast.showToast(this, R.string.imei_code_error)
            }
        }

    // 搜索型号界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                    result.data?.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                        ?.let { json ->
                            GsonUtils.json2List(json, SearchSelectParam::class.java)
                                ?.let { selected ->
                                    if (selected.isNotEmpty()) {
                                        mViewModel.createDeviceShop.value = selected[0]
                                    }
                                }
                        }
                }
                DeviceModelActivity.ResultCode -> {
                    result.data?.let { intent ->
                        mViewModel.changeDeviceModel(
                            intent.getIntExtra(DeviceModelActivity.ResultDataId, -1),
                            intent.getStringExtra(DeviceModelActivity.ResultDataName),
                            intent.getIntExtra(DeviceCategory.CategoryId, -1),
                            intent.getStringExtra(DeviceCategory.CategoryCode),
                            intent.getIntExtra(DeviceCategory.CommunicationType, -1)
                        )
                        mViewModel.isIgnorePayCodeFlag =
                            intent.getBooleanExtra(DeviceCategory.IgnorePayCodeFlag, false)
                        checkIgnorePayCode()
                        mViewModel.isDispenser.value =
                            intent.getStringExtra(DeviceCategory.CategoryCode)
                                .equals(Dispenser)

                    }
                }
                IntentParams.DeviceFunctionConfigurationParams.ResultCode -> {
                    result.data?.let { intent ->
                        IntentParams.DeviceFunctionConfigurationParams.parseSkuFuncConfiguration(
                            intent
                        )?.let {
                            mViewModel.createDeviceFunConfigure.value = it
                        }
                    }
                }
                DropperAddSettingActivity.ResultCode -> {
                    result.data?.let { intent ->
                        GsonUtils.json2List(
                            intent.getStringExtra(
                                DropperAddSettingActivity.ResultData
                            ), SkuFuncConfigurationParam::class.java
                        )?.let {
                            mViewModel.createDeviceFunConfigure.value = it
                        }
                    }
                }

            }
        }

    override fun onResume() {
        super.onResume()
        LiveDataBus.with(BusEvents.SCAN_CHANGE_STATUS, Boolean::class.java)?.observe(this) {
            startQRActivity(it)
        }
    }

    override fun onPause() {
        super.onPause()
        LiveDataBus.remove(BusEvents.SCAN_CHANGE_STATUS)
    }

    override fun layoutId(): Int = R.layout.activity_device_create

    override fun backBtn(): View = mBinding.barDeviceCreateTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 付款码
        mBinding.mtivDeviceCreatePayCode.onSelectedEvent = {
            isAttrImei = false
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        // IMEI
        mBinding.mtivDeviceCreateImei.onSelectedEvent = {
            isAttrImei = false
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        // 洗衣机IMEI
        mBinding.mtivDeviceWashImei.onSelectedEvent = {
            isAttrImei = true
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        // 设备型号
        mBinding.mtivDeviceCreateModel.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceCreateActivity,
                    DeviceModelActivity::class.java
                )
            )
        }

        // 所属门店
        mBinding.mtivDeviceCreateDepartment.onSelectedEvent = {
            startNext.launch(Intent(
                this@DeviceCreateActivity,
                SearchSelectRadioActivity::class.java
            ).apply {
                putExtras(putExtras(IntentParams.SearchSelectTypeParam.pack(IntentParams.SearchSelectTypeParam.SearchSelectTypeShop)))
            })
        }

        // 功能配置
        mBinding.mtivDeviceCreateFunConfigure.onSelectedEvent = {
            if (mViewModel.isDispenser.value!!) {
                startNext.launch(Intent(
                    this@DeviceCreateActivity, DropperAddSettingActivity::class.java
                ).apply {
                    putExtra(
                        DropperAddSettingActivity.SpuId,
                        mViewModel.createAndUpdateEntity.value?.spuId
                    )
                    putExtra(
                        DropperAddSettingActivity.Deviceid,
                        mViewModel.createAndUpdateEntity.value?.id
                    )
                    mViewModel.createDeviceFunConfigure.value?.let { configs ->
                        putExtra(
                            DeviceFunctionConfigurationActivity.OldFuncConfiguration,
                            GsonUtils.any2Json(configs)
                        )
                    }
                })
            } else if (DeviceCategory.isDrinking(mViewModel.deviceCategoryCode)) {
                startNext.launch(
                    Intent(
                        this@DeviceCreateActivity,
                        DeviceDrinkingFunctionConfigurationActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.DeviceFunctionConfigurationParams.pack(
                                spuId = mViewModel.createAndUpdateEntity.value?.spuId,
                                categoryCode = mViewModel.deviceCategoryCode,
                                oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                            )
                        )
                    }
                )
            } else if (DeviceCategory.isShower(mViewModel.deviceCategoryCode)) {
                startNext.launch(
                    Intent(
                        this@DeviceCreateActivity,
                        DeviceShowerFunctionConfigurationActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.DeviceFunctionConfigurationParams.pack(
                                spuId = mViewModel.createAndUpdateEntity.value?.spuId,
                                categoryCode = mViewModel.deviceCategoryCode,
                                oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                            )
                        )
                    }
                )
            } else {
                startNext.launch(Intent(
                    this@DeviceCreateActivity,
                    DeviceFunctionConfigurationActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.DeviceFunctionConfigurationParams.pack(
                            spuId = mViewModel.createAndUpdateEntity.value?.spuId,
                            categoryCode = mViewModel.deviceCategoryCode,
                            communicationType = mViewModel.deviceCommunicationType,
                            oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                        )
                    )
                })
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        // 付款码
        mViewModel.payCode.observe(this) {
            mViewModel.createAndUpdateEntity.value?.code = it
        }
        // IMEI
        mViewModel.imeiCode.observe(this) {
            // 如果符合规则就查询型号
            if (StringUtils.isImeiCode(it)) {
                mBinding.mtivDeviceCreateImei.clearFocus()
                mViewModel.createAndUpdateEntity.value?.imei = it
                mViewModel.requestModelOfImei(it)
            } else {
                mViewModel.changeDeviceModel()
            }
        }
        // 洗衣机IMEI
        mViewModel.washimeiCode.observe(this) {
            mViewModel.createAndUpdateEntity.value?.washerImei = it
        }

        //设备名称
        mViewModel.deviceName.observe(this) {
            mViewModel.createAndUpdateEntity.value?.name = it
        }

        // 门店
        mViewModel.createDeviceShop.observe(this) {
            mViewModel.createAndUpdateEntity.value?.shopId = it.id
        }

        // 功能配置
        mViewModel.createDeviceFunConfigure.observe(this) {
            it?.let { list ->
                mBinding.llDeviceCreateSelectFunConfiguration.removeAllViews()
                val inflater = LayoutInflater.from(this@DeviceCreateActivity)
                if (mViewModel.isDispenser.value!!) {
                    list.flatMap { item ->
                        GsonUtils.json2List(item.extAttr, DosingConfigs::class.java)
                            ?: listOf()
                    }.forEachIndexed { _, config ->
                        val mFuncConfigBinding =
                            DataBindingUtil.inflate<ItemDeviceDetailDisposeMinBinding>(
                                inflater,
                                R.layout.item_device_detail_dispose_min,
                                null,
                                false
                            )
                        mFuncConfigBinding.item = config
                        mBinding.llDeviceCreateSelectFunConfiguration.addView(
                            mFuncConfigBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }
                } else if (DeviceCategory.isDrinkingOrShower(mViewModel.deviceCategoryCode)) {
                    buildDrinkingConfigureItemView(list, inflater)
                } else {
                    list.forEachIndexed { _, config ->
                        val mFuncConfigBinding =
                            DataBindingUtil.inflate<ItemSelectedDeviceFuncationConfigurationBinding>(
                                inflater,
                                R.layout.item_selected_device_funcation_configuration,
                                null,
                                false
                            )
                        mFuncConfigBinding.item = config
                        mFuncConfigBinding.isDryer =
                            DeviceCategory.isDryerOrHair(mViewModel.deviceCategoryCode)
                        mFuncConfigBinding.deviceCommunicationType =
                            mViewModel.deviceCommunicationType
                        mFuncConfigBinding.tvSelectFuncConfigurationFeature.visibility =
                            if (DeviceCategory.isHair(mViewModel.deviceCategoryCode)) View.GONE else View.VISIBLE
                        mBinding.llDeviceCreateSelectFunConfiguration.addView(
                            mFuncConfigBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }
                }
            }
        }

        // 跳转
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    /**
     * 构建饮水配置界面
     */
    private fun buildDrinkingConfigureItemView(
        list: List<SkuFuncConfigurationParam>,
        inflater: LayoutInflater,
    ) {
        list.firstOrNull()?.let { first ->
            GsonUtils.json2Class(first.extAttr, ExtAttrDrinkBean::class.java)
                ?.let { firstAttr ->
                    // 计价界面
                    DataBindingUtil.inflate<ItemSelectedDrinkDeviceFuncationConfigurationBinding>(
                        inflater,
                        R.layout.item_selected_drink_device_funcation_configuration,
                        null,
                        false
                    ).let { binding ->
                        binding.title =
                            com.lsy.framelib.utils.StringUtils.getString(
                                if (1 == firstAttr.priceCalculateMode) R.string.for_quantity
                                else R.string.for_time
                            ) + "计价"
                        binding.content = "${
                            com.lsy.framelib.utils.StringUtils.getString(R.string.over_time) +
                                    com.lsy.framelib.utils.StringUtils.getString(
                                        R.string.unit_minute_num_str,
                                        firstAttr.overTime
                                    )
                        }\n${
                            com.lsy.framelib.utils.StringUtils.getString(R.string.pause_time) +
                                    com.lsy.framelib.utils.StringUtils.getString(
                                        R.string.unit_minute_num_str,
                                        firstAttr.pauseTime
                                    )
                        }${
                            if (1 == firstAttr.priceCalculateMode) {
                                "\n" + com.lsy.framelib.utils.StringUtils.getString(R.string.single_pulse_quantity) +
                                        com.lsy.framelib.utils.StringUtils.getString(
                                            R.string.unit_quantity_num_str,
                                            firstAttr.singlePulseQuantity
                                        )
                            } else ""
                        }"
                        binding.soldState = 0
                        mBinding.llDeviceCreateSelectFunConfiguration.addView(
                            binding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }

                    // 单价界面
                    list.forEach { sku ->
                        DataBindingUtil.inflate<ItemSelectedDrinkDeviceFuncationConfigurationBinding>(
                            inflater,
                            R.layout.item_selected_drink_device_funcation_configuration,
                            null,
                            false
                        ).let { binding ->
                            binding.title =
                                sku.name + com.lsy.framelib.utils.StringUtils.getString(R.string.price)
                            binding.content =
                                "${
                                    String.format(
                                        "%.2f",
                                        sku.price
                                    )
                                }${com.lsy.framelib.utils.StringUtils.getString(if (1 == firstAttr.priceCalculateMode) R.string.unit_water_quantity_price_hint else R.string.unit_water_time_price_hint)}"
                            binding.soldState = sku.soldState
                            mBinding.llDeviceCreateSelectFunConfiguration.addView(
                                binding.root,
                                LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )
                        }
                    }
                }
        }
    }

    override fun initData() {
    }

    private fun checkIgnorePayCode() {
        if (mViewModel.isIgnorePayCodeFlag) {
            mViewModel.payCode.value = mViewModel.imeiCode.value ?: ""
        }
    }
}