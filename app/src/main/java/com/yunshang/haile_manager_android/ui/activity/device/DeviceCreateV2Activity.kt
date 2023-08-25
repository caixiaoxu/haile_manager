package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
import com.yunshang.haile_manager_android.databinding.ActivityDeviceCreateV2Binding
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
            if (mViewModel.isDispenser.value!!) {
                startNext.launch(Intent(
                    this, DropperAddSettingActivity::class.java
                ).apply {
                    putExtra(
                        DropperAddSettingActivity.SpuId,
                        mViewModel.spuId
                    )
                    mViewModel.createDeviceFunConfigure.value?.let { configs ->
                        putExtra(
                            DeviceFunctionConfigurationActivity.OldFuncConfiguration,
                            GsonUtils.any2Json(configs)
                        )
                    }
                })
            } else if (DeviceCategory.isDrinking(mViewModel.categoryCode.value)) {
                startNext.launch(
                    Intent(
                        this,
                        DeviceDrinkingFunctionConfigurationActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.DeviceFunctionConfigurationParams.pack(
                                spuId = mViewModel.spuId,
                                categoryCode = mViewModel.categoryCode.value,
                                oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                            )
                        )
                    }
                )
            } else if (DeviceCategory.isShower(mViewModel.categoryCode.value)) {
                startNext.launch(
                    Intent(
                        this,
                        DeviceShowerFunctionConfigurationActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.DeviceFunctionConfigurationParams.pack(
                                spuId = mViewModel.spuId,
                                categoryCode = mViewModel.categoryCode.value,
                                oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                            )
                        )
                    }
                )
            } else {
                startNext.launch(Intent(
                    this,
                    DeviceFunctionConfigurationActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.DeviceFunctionConfigurationParams.pack(
                            spuId = mViewModel.spuId,
                            categoryCode = mViewModel.categoryCode.value,
                            communicationType = mViewModel.deviceCommunicationType,
                            oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                        )
                    )
                })
            }
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