package com.shangyun.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceCreateAndUpdateViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.data.arguments.DeviceCategory
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.shangyun.haile_manager_android.databinding.ActivityDeviceCreateAndUpdateBinding
import com.shangyun.haile_manager_android.databinding.ItemSelectedDeviceFuncationConfigurationBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.CustomCaptureActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.utils.StringUtils


class DeviceCreateAndUpdateActivity :
    BaseBusinessActivity<ActivityDeviceCreateAndUpdateBinding, DeviceCreateAndUpdateViewModel>() {

    private val mDeviceCreateAndUpdateViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceCreateAndUpdateViewModel::class.java]
    }

    // 付款码相机启动器
    private val payCodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            val payCode = StringUtils.getPayCode(it)
            payCode?.let { code ->
                mDeviceCreateAndUpdateViewModel.payCode.value = code
            } ?: SToast.showToast(this, R.string.pay_code_error)
        }
    }

    // IMEI相机启动器
    private val imeiLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            mDeviceCreateAndUpdateViewModel.imeiCode.value = it
        } ?: SToast.showToast(this, R.string.imei_code_error)
    }

    private val scanOptions: ScanOptions by lazy {
        ScanOptions().apply {
            captureActivity = CustomCaptureActivity::class.java
//            setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)// 扫码的类型,一维码，二维码，一/二维码，默认为一/二维码
            setPrompt("请对准二维码")//提示语
            setOrientationLocked(true)
            setCameraId(0) // 选择摄像头
            setBeepEnabled(true) // 开启声音
        }
    }

    // 搜索型号界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                SearchSelectRadioActivity.ShopResultCode -> {
                    result.data?.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                        GsonUtils.json2Class(json, SearchSelectParam::class.java)?.let { selected ->
                            mDeviceCreateAndUpdateViewModel.createDeviceShop.value = selected
                        }
                    }
                }
                DeviceModelActivity.ResultCode -> {
                    result.data?.let { intent ->
                        mDeviceCreateAndUpdateViewModel.changeDeviceModel(
                            intent.getIntExtra(DeviceModelActivity.ResultDataId, -1),
                            intent.getStringExtra(DeviceModelActivity.ResultDataName),
                            intent.getIntExtra(DeviceCategory.CategoryId, -1),
                            intent.getStringExtra(DeviceCategory.CategoryCode),
                            intent.getIntExtra(DeviceCategory.CommunicationType, -1)
                        )
                    }
                }
                DeviceFunctionConfigurationActivity.ResultCode -> {
                    result.data?.let { intent ->
                        GsonUtils.json2List(
                            intent.getStringExtra(
                                DeviceFunctionConfigurationActivity.ResultData
                            ), SkuFuncConfigurationParam::class.java
                        )?.let {
                            mDeviceCreateAndUpdateViewModel.createDeviceFunConfigure.value = it
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_create_and_update

    override fun getVM(): DeviceCreateAndUpdateViewModel = mDeviceCreateAndUpdateViewModel

    override fun backBtn(): View = mBinding.barDeviceCreateTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 付款码
        mBinding.mtivDeviceCreatePayCode.onSelectedEvent = {
            payCodeLauncher.launch(scanOptions)
        }

        // IMEI
        mBinding.mtivDeviceCreateImei.onSelectedEvent = {
            imeiLauncher.launch(scanOptions)
        }

        // 设备型号
        mBinding.mtivDeviceCreateModel.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceCreateAndUpdateActivity,
                    DeviceModelActivity::class.java
                )
            )

        }

        // 所属门店
        mBinding.mtivDeviceCreateDepartment.onSelectedEvent = {
            startNext.launch(Intent(
                this@DeviceCreateAndUpdateActivity,
                SearchSelectRadioActivity::class.java
            ).apply {
                putExtra(
                    SearchSelectRadioActivity.SearchSelectType,
                    SearchSelectRadioViewModel.SearchSelectTypeShop
                )
            })
        }

        // 功能配置
        mBinding.mtivDeviceCreateFunConfigure.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceCreateAndUpdateActivity,
                    DeviceFunctionConfigurationActivity::class.java
                ).apply {
                    putExtra(
                        DeviceFunctionConfigurationActivity.SpuId,
                        mDeviceCreateAndUpdateViewModel.createAndUpdateEntity.value?.spuId
                    )
                    putExtra(
                        DeviceCategory.CategoryCode,
                        mDeviceCreateAndUpdateViewModel.deviceCategoryCode
                    )
                    putExtra(
                        DeviceCategory.CommunicationType,
                        mDeviceCreateAndUpdateViewModel.deviceCommunicationType
                    )
                    mDeviceCreateAndUpdateViewModel.createDeviceFunConfigure.value?.let { configs ->
                        putExtra(
                            DeviceFunctionConfigurationActivity.OldFuncConfiguration,
                            GsonUtils.any2Json(configs)
                        )
                    }
                }
            )
        }
    }

    override fun initEvent() {
        super.initEvent()
        // 付款码
        mDeviceCreateAndUpdateViewModel.payCode.observe(this) {
            mDeviceCreateAndUpdateViewModel.createAndUpdateEntity.value?.code = it
        }
        // IMEI
        mDeviceCreateAndUpdateViewModel.imeiCode.observe(this) {
            mDeviceCreateAndUpdateViewModel.createAndUpdateEntity.value?.imei = it
            mDeviceCreateAndUpdateViewModel.requestModelOfImei(it)
        }

        //设备名称
        mDeviceCreateAndUpdateViewModel.deviceName.observe(this) {
            mDeviceCreateAndUpdateViewModel.createAndUpdateEntity.value?.name = it
        }

        // 门店
        mDeviceCreateAndUpdateViewModel.createDeviceShop.observe(this) {
            mDeviceCreateAndUpdateViewModel.createAndUpdateEntity.value?.shopId = it.id
        }

        // 功能配置
        mDeviceCreateAndUpdateViewModel.createDeviceFunConfigure.observe(this) {
            it?.let { list ->
                mBinding.llDeviceCreateSelectFunConfiguration.removeAllViews()
                val mtb = DimensionUtils.dip2px(this@DeviceCreateAndUpdateActivity, 12f)
                list.forEachIndexed { index, config ->
                    val selectFuncConfigItem =
                        LayoutInflater.from(this@DeviceCreateAndUpdateActivity)
                            .inflate(
                                R.layout.item_selected_device_funcation_configuration,
                                null,
                                false
                            )
                    val mFuncConfigBinding =
                        DataBindingUtil.bind<ItemSelectedDeviceFuncationConfigurationBinding>(
                            selectFuncConfigItem
                        )
                    mFuncConfigBinding?.let {
                        mFuncConfigBinding.item = config
                        mBinding.llDeviceCreateSelectFunConfiguration.addView(
                            mFuncConfigBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, if (0 == index) mtb else 0, 0, mtb)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun initData() {
        mBinding.vm = mDeviceCreateAndUpdateViewModel
    }
}