package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
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
import com.yunshang.haile_manager_android.ui.activity.common.CustomCaptureActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber


class DeviceCreateActivity :
    BaseBusinessActivity<ActivityDeviceCreateBinding, DeviceCreateViewModel>(
        DeviceCreateViewModel::class.java,
        BR.vm
    ) {

    // 扫码启动器
    private val codeLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.trim()?.let {
            Timber.i("扫码:$it")
            val payCode = StringUtils.getPayCode(it)
            payCode?.let { code ->
                mViewModel.payCode.value = code
            } ?: run {
                if (StringUtils.isImeiCode(it))
                    mViewModel.imeiCode.value = it
                else
                    SToast.showToast(this, R.string.scan_code_error)
            }
        }
    }

    // 洗衣机IMEI相机启动器
    private val washimeiLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.trim()?.let {
            Timber.i("IMEI:$it")
            if (StringUtils.isImeiCode(it)) mViewModel.washimeiCode.value = it
            else SToast.showToast(this, R.string.imei_code_error1)
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
                SearchSelectTypeParam.ShopResultCode -> {
                    result.data?.getStringExtra(SearchSelectTypeParam.ResultData)?.let { json ->
                        GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
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
                        mViewModel.isDispenser.value =
                            intent.getStringExtra(DeviceCategory.CategoryCode).equals(Dispenser)

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

    override fun layoutId(): Int = R.layout.activity_device_create

    override fun backBtn(): View = mBinding.barDeviceCreateTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 付款码
        mBinding.mtivDeviceCreatePayCode.onSelectedEvent = {
            codeLauncher.launch(scanOptions)
        }

        // IMEI
        mBinding.mtivDeviceCreateImei.onSelectedEvent = {
            codeLauncher.launch(scanOptions)
        }

        // 洗衣机IMEI
        mBinding.mtivDeviceWashImei.onSelectedEvent = {
            washimeiLauncher.launch(scanOptions)
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
                putExtras(putExtras(SearchSelectTypeParam.pack(SearchSelectTypeParam.SearchSelectTypeShop)))
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
            } else {
                startNext.launch(Intent(
                    this@DeviceCreateActivity, DeviceFunctionConfigurationActivity::class.java
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
            mViewModel.createAndUpdateEntity.value?.imei = it
            mViewModel.requestModelOfImei(it)
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
                val mtb = DimensionUtils.dip2px(this@DeviceCreateActivity, 12f)
                if (mViewModel.isDispenser.value!!) {
                    var dosingconfigs = ArrayList<DosingConfigs>()
                    list.forEach { it ->
                        var disings = GsonUtils.json2List(it.extAttr, DosingConfigs::class.java)
                        disings?.let { it1 -> dosingconfigs.addAll(it1) }
                    }
                    dosingconfigs.forEachIndexed { index, config ->
                        val selectFuncConfigItem =
                            LayoutInflater.from(this@DeviceCreateActivity).inflate(
                                R.layout.item_device_detail_dispose_min, null, false
                            )
                        val mFuncConfigBinding =
                            DataBindingUtil.bind<ItemDeviceDetailDisposeMinBinding>(
                                selectFuncConfigItem
                            )
                        mFuncConfigBinding?.let {
                            mFuncConfigBinding.item = config

                            mFuncConfigBinding.tvTime.text =
                                " 单次用量 ${config.amount}ml/${config.price}元"
                            mFuncConfigBinding.tvState.text =
                                if (config.isOn) "启用中" else "已停用"
                            mFuncConfigBinding.tvState.setTextColor(Color.parseColor(if (config.isOn) "#F0A258" else "#999999"))

                            mBinding.llDeviceCreateSelectFunConfiguration.addView(mFuncConfigBinding.root,
                                LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(0, if (0 == index) mtb else 0, 0, mtb)
                                })
                        }
                    }
                } else if (DeviceCategory.isDrinking(mViewModel.deviceCategoryCode)) {
                    buildDrinkingConfigureItemView(list, inflater)
                } else {
                    list.forEachIndexed { index, config ->
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
}