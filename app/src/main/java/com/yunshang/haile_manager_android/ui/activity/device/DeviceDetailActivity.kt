package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceDetailModel
import com.yunshang.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.Item
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.databinding.ActivityDeviceDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDetailDisposeMinBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDetailFuncPriceBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.CustomCaptureActivity
import com.yunshang.haile_manager_android.ui.activity.device.DropperAddSettingActivity.Companion.OldFuncConfiguration
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class DeviceDetailActivity :
    BaseBusinessActivity<ActivityDeviceDetailBinding, DeviceDetailModel>(
        DeviceDetailModel::class.java,
        BR.vm
    ) {

    companion object {
        const val GoodsId = "goodsId"
    }

    // 跳转修改界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                DeviceMultiChangeActivity.ResultCode -> {
                    result.data?.let { intent ->
                        intent.getStringExtra(DeviceMultiChangeActivity.ResultData)?.let {
                            when (intent.getIntExtra(DeviceMultiChangeViewModel.Type, -1)) {
                                DeviceMultiChangeViewModel.typeChangeModel -> {
                                    mViewModel.deviceDetail.value?.imei = it
                                    mViewModel.imei.value = it
                                }

                                DeviceMultiChangeViewModel.typeChangePayCode -> {
                                    mViewModel.deviceDetail.value?.code = it
                                    mViewModel.code.value = it
                                }

                                DeviceMultiChangeViewModel.typeChangeName -> {
                                    mViewModel.deviceDetail.value?.name = it
                                    mViewModel.name.value = it
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_detail

    override fun backBtn(): View = mBinding.barDeviceDetailTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceDetailTitle.getRightBtn().run {
            setText(R.string.advanced_setup)
            setTextColor(
                ContextCompat.getColor(
                    this@DeviceDetailActivity,
                    R.color.colorPrimary
                )
            )
            setOnClickListener {
                mViewModel.deviceAdvancedValues.value?.let { values ->
                    // 高级设置
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceAdvancedActivity::class.java
                        ).apply {
                            putExtra(
                                DeviceAdvancedActivity.GoodId,
                                mViewModel.goodsId
                            )
                            putExtra(
                                DeviceAdvancedActivity.Advanced,
                                GsonUtils.any2Json(values)
                            )
                        }
                    )
                }
            }
        }
    }

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodsId = intent.getIntExtra(GoodsId, -1)
    }

    override fun initEvent() {
        super.initEvent()

        val mTB = DimensionUtils.dip2px(this, 12f)
        mViewModel.deviceDetail.observe(this) { detail ->
            if (!detail.shopAppointmentEnabled) {
                mBinding.glDeviceDetailFunc.children.find {
                    it.tag == R.mipmap.icon_device_device_appointment_setting
                }?.let {
                    mBinding.glDeviceDetailFunc.removeView(it)
                }
            }

            // 吹风机不显示桶自洁
            if (DeviceCategory.isHair(detail.categoryCode)) {
                mViewModel.deviceDetailFunOperate.find { item -> item.icon == R.mipmap.icon_device_self_clean }
                    ?.let { item ->
                        item.show.value = false
                    }
            }
            // 投放器不显示部分icon
            if (DeviceCategory.isDispenser(detail.categoryCode)) {
                mViewModel.deviceDetailFunOperate.forEach { item ->
                    if (item.icon == R.mipmap.icon_device_self_clean || item.icon == R.mipmap.icon_device_change_model || item.icon == R.mipmap.icon_device_change_pay_code || item.icon == R.mipmap.icon_device_create_pay_code || item.icon == R.mipmap.icon_device_device_appointment_setting) {
                        item.show.value = false
                    }
                }
            } else {
                mViewModel.deviceDetailFunOperate.forEach { item ->
                    if (item.icon == R.mipmap.icon_device_device_selfclean || item.icon == R.mipmap.icon_device_device_drain || item.icon == R.mipmap.icon_device_device_voice || item.icon == R.mipmap.icon_device_device_unblanking) {
                        item.show.value = false
                    }
                }
            }

            mBinding.llDeviceDetailFuncPrice.removeAllViews()
            if (mViewModel.isDispenser(detail.categoryCode)) {
                var dosingconfigs = ArrayList<DosingConfigs>()
                detail?.dosingVOS?.forEach {
                    dosingconfigs.addAll(it.configs)
                }
                dosingconfigs?.forEachIndexed { index, item ->
                    val itemBinding = LayoutInflater.from(this@DeviceDetailActivity)
                        .inflate(R.layout.item_device_detail_dispose_min, null, false).let { view ->
                            DataBindingUtil.bind<ItemDeviceDetailDisposeMinBinding>(view)
                        }
                    itemBinding?.let {
                        itemBinding.item = item
                        mBinding.llDeviceDetailFuncPrice.addView(itemBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            ).apply {
                                setMargins(0, if (0 == index) mTB else 0, 0, mTB)
                            })
                        itemBinding.tvTime.text = " 单次用量 ${item.amount}ml/${item.price}元"
                        itemBinding.tvState.text = if (item.isOn) "启用中" else "已停用"
                        itemBinding.tvState.setTextColor(Color.parseColor(if (item.isOn) "#F0A258" else "#999999"))
                    }
                }
            } else {
                detail?.items?.forEachIndexed { index, item ->
                    val itemBinding = LayoutInflater.from(this@DeviceDetailActivity)
                        .inflate(R.layout.item_device_detail_func_price, null, false).let { view ->
                            DataBindingUtil.bind<ItemDeviceDetailFuncPriceBinding>(view)
                        }
                    itemBinding?.let {
                        itemBinding.item = item
                        itemBinding.isDryer = DeviceCategory.isDryerOrHair(detail.categoryCode)
                        itemBinding.deviceCommunicationType = detail.communicationType
                        itemBinding.tvFunPriceDesc.visibility =
                            if (DeviceCategory.isHair(detail.categoryCode)) View.GONE else View.VISIBLE
                        mBinding.llDeviceDetailFuncPrice.addView(itemBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            ).apply {
                                setMargins(0, if (0 == index) mTB else 0, 0, mTB)
                            })
                    }
                }
            }

            mBinding.llDeviceDetailFuncRelated.removeAllViews()
            if (mViewModel.deviceDetail.value?.showRelated()!!) {
                var dosingconfigs = ArrayList<DosingConfigs>()
                detail?.relatedGoodsDetailVo?.dosingVOS?.forEach {
                    dosingconfigs.addAll(it.configs)
                }
                dosingconfigs?.forEachIndexed { index, item ->
                    val itemBinding = LayoutInflater.from(this@DeviceDetailActivity)
                        .inflate(R.layout.item_device_detail_dispose_min, null, false).let { view ->
                            DataBindingUtil.bind<ItemDeviceDetailDisposeMinBinding>(view)
                        }
                    itemBinding?.let {
                        itemBinding.item = item
                        mBinding.llDeviceDetailFuncRelated.addView(itemBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            ).apply {
                                setMargins(0, if (0 == index) mTB else 0, 0, mTB)
                            })
                        itemBinding.tvTime.text = " 单次用量 ${item.amount}ml/${item.price}元"
                        itemBinding.tvState.text = if (item.isOn) "启用中" else "已停用"
                        itemBinding.tvState.setTextColor(Color.parseColor(if (item.isOn) "#F0A258" else "#999999"))
                    }
                }
            }

            mViewModel.categoryCode.value = detail.categoryCode

            if (null == detail.deviceAttributeVo) {

            }
            mBinding.tvLaundryTemperature.text =
                if (null == detail.deviceAttributeVo) "0°C" else "${detail.deviceAttributeVo.nowTemperature}°C"
        }

        // 是否有高级参数设置
        mViewModel.deviceAdvancedValues.observe(this) {
            if (!it.isNullOrEmpty()) {
                initRightBtn()
            }
        }

        mViewModel.jump.observe(this) {
            when (it) {
                0 -> finish()
                // 修改功能价格
                1 -> {
                    if (mViewModel.isDispenser(mViewModel.categoryCode.value)) {
                        mViewModel.deviceDetail.value?.let { detail ->
                            startActivity(Intent(
                                this@DeviceDetailActivity,
                                DropperAddSettingActivity::class.java
                            ).apply {
                                putExtra(
                                    DropperAddSettingActivity.SpuId,
                                    detail.spuId
                                )
                                putExtra(
                                    DropperAddSettingActivity.Deviceid,
                                    detail.id
                                )
                                var skufuncs = ArrayList<SkuFuncConfigurationParam>()
                                detail.items.forEach { it ->
                                    var sku = SkuFuncConfigurationParam(
                                        it.skuId,
                                        it.name,
                                        it.price.toDouble(),
                                        (if (it.pulse.isNullOrEmpty()) 0 else it.pulse.toInt()),
                                        it.unit.toInt(),
                                        it.extAttr,
                                        it.feature,
                                        it.soldState,
                                        ""
                                    )
                                    skufuncs.add(sku)
                                }
                                putExtra(
                                    OldFuncConfiguration,
                                    GsonUtils.any2Json(skufuncs)
                                )

                            })
                        }
                    } else {
                        mViewModel.deviceDetail.value?.let { detail ->
                            startActivity(
                                Intent(
                                    this@DeviceDetailActivity,
                                    DeviceFunctionConfigurationActivity::class.java
                                ).apply {
                                    putExtras(
                                        IntentParams.DeviceFunctionConfigurationParams.pack(
                                            goodId = mViewModel.goodsId,
                                            spuId = detail.spuId,
                                            categoryCode = detail.categoryCode,
                                            communicationType = detail.communicationType,
                                            oldFuncConfiguration = detail.items.mapNotNull { item -> item.changeConfigurationParam() }
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                // 启动
                2 -> mViewModel.deviceDetail.value?.let { detail ->
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceStartActivity::class.java
                        ).apply {
                            putExtra(DeviceStartActivity.Imei, detail.imei)
                            putExtra(DeviceCategory.CategoryCode, detail.categoryCode)
                            putExtra(DeviceStartActivity.Items, GsonUtils.any2Json(detail.items))
                        })
                }
                // 更换模块
                3 -> startNext.launch(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceMultiChangeActivity::class.java
                    ).apply {
                        putExtra(
                            DeviceMultiChangeActivity.GoodId,
                            mViewModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangeModel
                        )
                    }
                )
                // 更换付款码
                4 -> startNext.launch(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceMultiChangeActivity::class.java
                    ).apply {
                        putExtra(
                            DeviceMultiChangeActivity.GoodId,
                            mViewModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangePayCode
                        )
                    }
                )
                // 更换名称
                5 -> startNext.launch(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceMultiChangeActivity::class.java
                    ).apply {
                        putExtra(
                            DeviceMultiChangeActivity.GoodId,
                            mViewModel.goodsId
                        )
                        putExtra(
                            DeviceMultiChangeViewModel.Type,
                            DeviceMultiChangeViewModel.typeChangeName
                        )
                    }
                )
                // 生成付款码
                6 -> mViewModel.deviceDetail.value?.let { detail ->
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DevicePayCodeActivity::class.java
                        ).apply {
                            putExtra(DevicePayCodeActivity.Code, detail.scanUrl)
                        })
                }
                // 显示预约
                7 -> mViewModel.deviceDetail.value?.let { detail ->
                    if (20 == detail.communicationType) {
                        SToast.showToast(this@DeviceDetailActivity, "当前功能不支撑脉冲设备");
                        return@let
                    }
                    if (null == detail.appointmentEnabled) {
                        SToast.showToast(this@DeviceDetailActivity, "当前功能不可用");
                        return@let
                    }
                    CommonDialog.Builder(
                        "是否${if (detail.appointmentEnabled!!) "关闭" else "开启"}该设备预约功能"
                    ).apply {
                        negativeTxt = "否"
                        setPositiveButton("是") {
                            mViewModel.openOrCloseAppointment(!detail.appointmentEnabled!!) {
                                SToast.showToast(
                                    this@DeviceDetailActivity,
                                    if (!detail.appointmentEnabled!!) R.string.open_success else R.string.close_success
                                )
                                detail.appointmentEnabled = !detail.appointmentEnabled!!
                            }
                        }
                    }.build().show(supportFragmentManager)
                }
                // 复位
                8 -> mViewModel.deviceDetail.value?.let { detail ->
                    if (DeviceCategory.isHair(detail.categoryCode)) {
                        CommonBottomSheetDialog.Builder(
                            StringUtils.getString(R.string.restart_dialog_title),
                            detail.items.filter { item -> 1 == item.soldState }
                        ).apply {
                            onValueSureListener =
                                object : CommonBottomSheetDialog.OnValueSureListener<Item> {
                                    override fun onValue(data: Item?) {
                                        mViewModel.deviceOperate(0, data?.id)
                                    }
                                }
                        }.build().show(supportFragmentManager)
                    } else {
                        mViewModel.deviceOperate(0)
                    }
                }

                9 -> mViewModel.deviceDetail.value?.let { detail ->
                    mViewModel.deviceOpenCap(detail.imei)
                }

                10 -> mViewModel.deviceDetail.value?.let { detail ->
                    //语音设置
                    startNext.launch(Intent(
                        this@DeviceDetailActivity, DropperVoiceActivity::class.java
                    ).apply {
                        putExtra(DropperVoiceActivity.Deviceimei, detail.imei)
                        putExtra("process", detail.deviceAttributeVo.volume)
                        putExtra(
                            "preventDisturbSwitch",
                            detail.deviceAttributeVo.preventDisturbSwitch
                        )
                        putExtra(
                            "voiceBroadcastStatus",
                            detail.deviceAttributeVo.voiceBroadcastStatus
                        )
                        putExtra(
                            "preventDisturbStartTime",
                            detail.deviceAttributeVo.preventDisturbStartTime
                        )
                        putExtra(
                            "preventDisturbEndTime",
                            detail.deviceAttributeVo.preventDisturbStopTime
                        )
                    })
                }

                11 -> mViewModel.deviceDetail.value?.let { detail ->
                    mViewModel.deviceSetting(20, detail.imei)
                }

                12 -> mViewModel.deviceDetail.value?.let { detail ->
                    mViewModel.deviceSetting(30, detail.imei)
                }


            }
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.DEVICE_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestData(1)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 初始化功能操作区
        val itemW = ScreenUtils.screenWidth / mBinding.glDeviceDetailFunc.columnCount
        val pLR = DimensionUtils.dip2px(this, 8f)
        val pTB = DimensionUtils.dip2px(this, 16f)
        mViewModel.deviceDetailFunOperate.forEachIndexed { index, config ->
            (LayoutInflater.from(this@DeviceDetailActivity)
                .inflate(R.layout.item_device_detail_func, null, false) as AppCompatTextView).also {
                it.text = config.title
                it.tag = config.icon
                it.setCompoundDrawablesWithIntrinsicBounds(0, config.icon, 0, 0)
                it.setPadding(
                    pLR,
                    if (0 == index % mBinding.glDeviceDetailFunc.columnCount) pTB else 0,
                    pLR,
                    pTB
                )
                config.show.observe(this) { show ->
                    if (!show) {
                        mBinding.glDeviceDetailFunc.removeView(it)
                    }
                }
                it.setOnClickListener(config.onClick)
                mBinding.glDeviceDetailFunc.addView(
                    it, ViewGroup.LayoutParams(itemW, ViewGroup.LayoutParams.WRAP_CONTENT)
                )
            }
        }

        mBinding.btnDeviceDetailDelete.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.device_delete_hint)).apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.delete)) {
                    mViewModel.deviceDelete()
                }
            }.build().show(supportFragmentManager)
        }

        mBinding.tvTemperatureLimit.setOnClickListener {
            startActivity(Intent(
                this@DeviceDetailActivity, DropperTemperatureActivity::class.java
            ).apply {
                putExtra(
                    "max", "${mViewModel.deviceDetail.value?.deviceAttributeVo?.maxTemperature}"

                )
                putExtra(
                    "min", "${mViewModel.deviceDetail.value?.deviceAttributeVo?.minTemperature}"
                )
                putExtra(
                    "imei", "${mViewModel.deviceDetail.value?.imei}"
                )
                putExtra(
                    "temperatureSwitch",
                    mViewModel.deviceDetail.value?.deviceAttributeVo?.temperatureSwitch
                )
            })

        }

        mBinding.tvOpencap1.setOnClickListener {
            activate1Launcher.launch(scanOptions)
        }
        mBinding.tvOpencap2.setOnClickListener {
            activate2Launcher.launch(scanOptions)
        }
        mBinding.includeError.tvBaseInfoContentFind.setOnClickListener {
            startActivity(Intent(
                this@DeviceDetailActivity, OrderDetailActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.OrderDetailParams.pack(
                        mViewModel.deviceDetail.value!!.errorDeviceOrderId
                    )
                )
            })
        }

    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestData()
    }

    // 扫描投放器液体核销码
    private val activate1Launcher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.trim()?.let {
            mViewModel.deviceActivate(1, it)
        }
    }
    private val activate2Launcher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.trim()?.let {
            mViewModel.deviceActivate(2, it)
        }
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


}