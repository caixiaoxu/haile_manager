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
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceCreateV2ViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.ExtAttrDrinkBean
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.databinding.ActivityDeviceCreateV2Binding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDetailDisposeMinBinding
import com.yunshang.haile_manager_android.databinding.ItemSelectedDeviceFuncationConfigurationBinding
import com.yunshang.haile_manager_android.databinding.ItemSelectedDrinkDeviceFuncationConfigurationBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber

/**
 * 设备增加新流程修改，废弃旧流程DeviceCreateActivity
 */
class DeviceCreateV2Activity :
    BaseBusinessActivity<ActivityDeviceCreateV2Binding, DeviceCreateV2ViewModel>(
        DeviceCreateV2ViewModel::class.java, BR.vm
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
        startQRCodeScan.launch(
            Intent(
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
                        if (StringUtils.isImeiCode(it)) mViewModel.washImeiCode.value = it
                        else SToast.showToast(this, R.string.imei_code_error1)
                    } else {
                        mViewModel.codeStr = it
                        StringUtils.getPayImeiCode(it)?.let { code ->
                            mViewModel.payCode.value = code
                            mViewModel.imeiCode.value = code
                        } ?: run {
                            val payCode = StringUtils.getPayCode(it)
                            if (null != payCode) {
                                mViewModel.payCode.value = payCode
                            } else if (StringUtils.isImeiCode(it)) {
                                mViewModel.imeiCode.value = it
                                mBinding.itemDeviceCreateImei.clearFocus()
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
                IntentParams.DeviceCategoryModelParams.ResultCode -> {
                    result.data?.let { intent ->
                        mViewModel.initDeviceCategoryAndModel(
                            IntentParams.DeviceCategoryModelParams.parseSpuId(intent),
                            IntentParams.DeviceCategoryModelParams.parseCategoryName(intent),
                            IntentParams.DeviceCategoryModelParams.parseDeviceFeature(intent),
                            IntentParams.DeviceCategoryModelParams.parseCategoryId(intent),
                            IntentParams.DeviceCategoryModelParams.parseCategoryCode(intent),
                            IntentParams.DeviceCategoryModelParams.parseCommunicationType(intent),
                            IntentParams.DeviceCategoryModelParams.parseIgnorePayCodeFlag(intent),
                            IntentParams.DeviceCategoryModelParams.parseExtAttrDtoJson(intent),
                        )
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

    override fun layoutId(): Int = R.layout.activity_device_create_v2

    override fun backBtn(): View = mBinding.barDeviceCreateV2Title.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.imeiCode.observe(this) {
            // 如果符合规则就查询型号
            if (StringUtils.isImeiCode(it)) {
                mBinding.itemDeviceCreateImei.clearFocus()
                mViewModel.requestModelOfImei(it)
            }
        }

        // 功能配置
        mViewModel.createDeviceFunConfigure.observe(this) {
            it?.let { list ->
                mBinding.llDeviceCreateSelectFunConfiguration.removeAllViews()
                val inflater = LayoutInflater.from(this)
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
                } else if (DeviceCategory.isDrinkingOrShower(mViewModel.categoryCode.value)) {
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
                            DeviceCategory.isDryerOrHair(mViewModel.categoryCode.value)
                        mFuncConfigBinding.deviceCommunicationType =
                            mViewModel.deviceCommunicationType
                        mFuncConfigBinding.tvSelectFuncConfigurationFeature.visibility =
                            if (DeviceCategory.isHair(mViewModel.categoryCode.value)) View.GONE else View.VISIBLE
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
                                sku.name + com.lsy.framelib.utils.StringUtils.getString(R.string.unit_price)
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

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRight()

        // IMEI
        mBinding.itemDeviceCreateImei.onSelectedEvent = {
            isAttrImei = false
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        // 付款码
        mBinding.itemDeviceCreatePayCode.onSelectedEvent = {
            isAttrImei = false
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        // 所属门店
        mBinding.itemDeviceCreateDepartment.onSelectedEvent = {
            startNext.launch(Intent(
                this,
                SearchSelectRadioActivity::class.java
            ).apply {
                putExtras(putExtras(IntentParams.SearchSelectTypeParam.pack(IntentParams.SearchSelectTypeParam.SearchSelectTypeShop)))
            })
        }

        // 设备型号
        mBinding.itemDeviceCreateCategory.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this,
                    DeviceModelActivity::class.java
                )
            )
        }
        mBinding.itemDeviceCreateModel.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this,
                    DeviceModelActivity::class.java
                )
            )
        }

        // 洗衣机IMEI
        mBinding.itemDeviceWashImei.onSelectedEvent = {
            isAttrImei = true
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        // 功能配置
        mBinding.itemDeviceCreateFunConfigure.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this,
                    DeviceFunConfigurationV2Activity::class.java
                ).apply {
                    putExtras(
                        IntentParams.DeviceFunConfigurationV2Params.pack(
                            mViewModel.spuId.value,
                            mViewModel.categoryCode.value,
                            mViewModel.deviceCommunicationType,
                            mViewModel.extAttrDtoJson.value
                        )
                    )
                }
            )
        }
    }

    private fun initRight() {
        mBinding.barDeviceCreateV2Title.getRightBtn(true).run {
            setText(R.string.scan_input)
            textSize = 14f
            setTextColor(Color.WHITE)
            val pH = DimensionUtils.dip2px(this@DeviceCreateV2Activity, 12f)
            val pV = DimensionUtils.dip2px(this@DeviceCreateV2Activity, 4f)
            setPadding(pH, pV, pH, pV)
            setCompoundDrawablesWithIntrinsicBounds(
                R.mipmap.icon_device_create_scan, 0, 0, 0
            )
            compoundDrawablePadding = pV
            setOnClickListener {
                isAttrImei = false
                requestMultiplePermission.launch(
                    SystemPermissionHelper.cameraPermissions()
                        .plus(SystemPermissionHelper.readWritePermissions())
                )
            }
        }
    }

    override fun initData() {
    }
}