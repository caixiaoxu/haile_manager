package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.SystemPermissionHelper
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceDetailModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.databinding.*
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderDetailActivity
import com.yunshang.haile_manager_android.ui.activity.order.OrderManagerActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import timber.log.Timber

class DeviceDetailActivity : BaseBusinessActivity<ActivityDeviceDetailBinding, DeviceDetailModel>(
    DeviceDetailModel::class.java, BR.vm
) {

    companion object {
        const val GoodsId = "goodsId"
    }

    // 跳转修改界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                    result.data?.let { intent ->
                        val positionId = IntentParams.ShopPositionSelectorParams.parseSelectList(
                            intent
                        )?.firstOrNull()?.positionList?.firstOrNull()?.id
                        mViewModel.transferDevice(this@DeviceDetailActivity, positionId)
                    }
                }

                IntentParams.DeviceParamsUpdateParams.ResultCode -> {
                    result.data?.let { intent ->
                        intent.getStringExtra(IntentParams.DeviceParamsUpdateParams.ResultData)
                            ?.let {
                                when (IntentParams.DeviceParamsUpdateParams.parseUpdateParamsType(
                                    intent
                                )) {
                                    IntentParams.DeviceParamsUpdateParams.typeChangeModel -> {
                                        mViewModel.deviceDetail.value?.imei = it
                                        mViewModel.imei.value = it
                                    }

                                    IntentParams.DeviceParamsUpdateParams.typeChangePayCode -> {
                                        mViewModel.deviceDetail.value?.code = it
                                        mViewModel.code.value = it
                                    }

                                    IntentParams.DeviceParamsUpdateParams.typeChangeName -> {
                                        mViewModel.deviceDetail.value?.name = it
                                        mViewModel.name.value = it
                                    }

                                    IntentParams.DeviceParamsUpdateParams.typeChangeFloor -> {
                                        mViewModel.deviceDetail.value?.floorCodeVal = it
                                    }
                                }
                            }
                    }
                }
            }
        }

    private fun preTransferDevices() {
        mViewModel.preTransferDevice() {
            if (0 == it) {
                transferDevices()
            } else {
                CommonDialog.Builder("该设备存在关联设备，转移操作，会同步转移关联的设备。若不需要则请先解除关联")
                    .apply {
                        title = StringUtils.getString(R.string.tip)
                        negativeTxt = StringUtils.getString(R.string.cancel)
                        setPositiveButton(StringUtils.getString(R.string.sure)) {
                            transferDevices()
                        }
                    }.build().show(supportFragmentManager)
            }
        }
    }

    private fun transferDevices() {
        startNext.launch(
            Intent(
                this@DeviceDetailActivity,
                ShopPositionSelectorActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.ShopPositionSelectorParams.pack(
                        canMultiSelect = false,
                        canSelectAll = false,
                        mustSelect = false,
                        title = "选择营业点"
                    )
                )
            }
        )
    }

    private var isDeviceActivateType: Int = 1

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

    // 扫描投放器液体核销码
    private val startQRCodeScan =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 扫码结果
            if (result.resultCode == RESULT_OK) {
                CameraScan.parseScanResult(result.data)?.let {
                    Timber.i("扫码:$it")
                    mViewModel.deviceActivate(isDeviceActivateType, it)
                } ?: SToast.showToast(this, R.string.imei_code_error)
            }
        }


    private val mFunAdapter by lazy {
        CommonRecyclerAdapter<ItemSelectFunConfigureV2Binding, SkuFunConfigurationV2Param>(
            R.layout.item_select_fun_configure_v2, BR.item
        ) { mItemBinding, _, item ->

            item.extAttrDto.items.filter { attr -> attr.isEnabled }.let {
                val isPulseDevice =
                    DeviceCategory.isPulseDevice(mViewModel.deviceDetail.value?.communicationType)
                mItemBinding?.llSelectFunConfigureAttrs?.buildChild<ItemSelectFunConfigureAttrItemV2Binding, ExtAttrDtoItem>(
                    it
                ) { index, childBinding, data ->
                    childBinding.type = 0
                    childBinding.title =
                        if (0 == index) StringUtils.getString(R.string.price_configure) + "：" else ""
                    childBinding.value =
                        "${data.unitPriceVal}元/${data.getTitle()}${if (isPulseDevice) "/${data.pulse}个" else ""}"
                    childBinding.isDefault = data.isDefault
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
                // 高级设置
                startActivity(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceAdvancedActivity::class.java
                    ).apply {
                        mViewModel.deviceAdvancedValues.value?.let { values ->
                            putExtras(
                                IntentParams.DeviceAdvanceParams.pack(
                                    mViewModel.goodsId,
                                    values
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodsId = intent.getIntExtra(GoodsId, -1)
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

    override fun initEvent() {
        super.initEvent()

        mViewModel.deviceDetail.observe(this) { detail ->
            mViewModel.deviceDetailFunOperate.forEach { item ->
                item.show.value = when (item.title) {
                    R.string.restart -> !DeviceCategory.isDrinking(detail.categoryCode)
                    R.string.start -> !DeviceCategory.isDrinkingOrShower(detail.categoryCode)
                    R.string.devices_self_clean -> DeviceCategory.isDispenser(detail.categoryCode)
                    R.string.self_clean -> DeviceCategory.isWashingOrShoes(detail.categoryCode)
                    R.string.change_model -> !DeviceCategory.isDispenser(detail.categoryCode)
                            && !DeviceCategory.isDrinkingOrShower(detail.categoryCode)

                    R.string.unlock1 -> DeviceCategory.isDrinkingOrShower(detail.categoryCode)
                    R.string.unlock -> DeviceCategory.isDispenser(detail.categoryCode)
                    R.string.change_pay_code -> !DeviceCategory.isDispenser(detail.categoryCode)
                            && !DeviceCategory.isDrinkingOrShower(detail.categoryCode)

                    R.string.create_pay_code -> !DeviceCategory.isDispenser(detail.categoryCode) && !DeviceCategory.isShower(
                        detail.categoryCode
                    )

                    R.string.device_transfer -> true
                    R.string.update_func_price -> true
                    R.string.update_device_name -> true
                    R.string.update_params_setting -> mViewModel.checkSinglePulseQuantity(detail)
                    R.string.device_appointment_setting -> 20 != detail.communicationType && detail.shopAppointmentEnabled
                    R.string.device_voice -> DeviceCategory.isDispenser(detail.categoryCode)
                    R.string.device_drain -> DeviceCategory.isDispenser(detail.categoryCode)
                    R.string.update_floor -> true
                    else -> false
                }
            }

            mBinding.glDeviceDetailFunc.visibility = View.VISIBLE

            // 功能配置
            mFunAdapter.refreshList(detail.items.filter { item -> 1 == item.soldState }
                .toMutableList(), true)

            // 关联设备的
            mBinding.llDeviceDetailFuncRelated.removeAllViews()
            if (mViewModel.deviceDetail.value?.showRelated()!!) {
                val inflater = LayoutInflater.from(this)
                detail?.relatedGoodsDetailVo?.dosingVOS?.flatMap { item ->
                    item.configs ?: listOf()
                }
                    ?.forEachIndexed { _, item ->
                        val itemBinding =
                            DataBindingUtil.inflate<ItemDeviceDetailDisposeMinBinding>(
                                inflater,
                                R.layout.item_device_detail_dispose_min,
                                null,
                                false
                            )
                        itemBinding.item = item
                        mBinding.llDeviceDetailFuncRelated.addView(
                            itemBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            )
                        )
                    }
            }
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
                1 ->
                    mViewModel.deviceDetail.value?.let { detail ->
                        startActivity(
                            Intent(
                                this,
                                DeviceFunConfigurationV2Activity::class.java
                            ).apply {
                                putExtras(
                                    IntentParams.DeviceFunConfigurationV2Params.pack(
                                        detail.spuId,
                                        detail.categoryCode,
                                        detail.communicationType,
                                        GsonUtils.any2Json(detail.spuDto?.extAttrDto),
                                        detail.items,
                                        mViewModel.goodsId,
                                        detail.shopId,
                                        StringUtils.getString(R.string.update_func_price)
                                    )
                                )
                            }
                        )
                    }
                // 启动
                2 -> mViewModel.deviceDetail.value?.let { detail ->
                    if (true == detail.auditFlag) {
                        SToast.showToast(this@DeviceDetailActivity, "解绑审批中，不可启用设备")
                        return@let
                    }
                    startActivity(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceStartActivity::class.java
                        ).apply {
                            putExtra(DeviceStartActivity.Imei, detail.imei)
                            putExtra(DeviceCategory.CategoryCode, detail.categoryCode)
                            putExtra(
                                DeviceStartActivity.Items,
                                GsonUtils.any2Json(detail.items)
                            )
                        })
                }
                // 更换模块
                3 -> mViewModel.deviceDetail.value?.let { detail ->
                    startNext.launch(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceMultiChangeActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.DeviceParamsUpdateParams.pack(
                                    GsonUtils.any2Json(detail.toUpdateParams()),
                                    IntentParams.DeviceParamsUpdateParams.typeChangeModel,
                                )
                            )
                        }
                    )
                }
                // 更换付款码
                4 -> mViewModel.deviceDetail.value?.let { detail ->
                    startNext.launch(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceMultiChangeActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.DeviceParamsUpdateParams.pack(
                                    GsonUtils.any2Json(detail.toUpdateParams()),
                                    IntentParams.DeviceParamsUpdateParams.typeChangePayCode,
                                )
                            )
                        }
                    )
                }
                // 更换名称
                5 -> mViewModel.deviceDetail.value?.let { detail ->
                    startNext.launch(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceMultiChangeActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.DeviceParamsUpdateParams.pack(
                                    GsonUtils.any2Json(detail.toUpdateParams()),
                                    IntentParams.DeviceParamsUpdateParams.typeChangeName,
                                    mViewModel.deviceDetail.value?.name
                                )
                            )
                        }
                    )
                }
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
                        SToast.showToast(this@DeviceDetailActivity, "当前功能不支撑脉冲设备")
                        return@let
                    }
                    if (null == detail.appointmentEnabled) {
                        SToast.showToast(this@DeviceDetailActivity, "当前功能不可用")
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
                                object :
                                    CommonBottomSheetDialog.OnValueSureListener<SkuFunConfigurationV2Param> {
                                    override fun onValue(data: SkuFunConfigurationV2Param?) {
                                        mViewModel.deviceOperate(0, data?.id)
                                    }
                                }
                        }.build().show(supportFragmentManager)
                    } else {
                        CommonDialog.Builder("确定复位此设备？").apply {
                            negativeTxt = StringUtils.getString(R.string.cancel)
                            setPositiveButton(StringUtils.getString(R.string.sure)) {
                                mViewModel.deviceOperate(0)
                            }
                        }.build().show(supportFragmentManager)
                    }
                }
                // 开锁
                9 -> mViewModel.deviceDetail.value?.let { detail ->
                    if (DeviceCategory.isDrinkingOrShower(detail.categoryCode)) {
                        CommonDialog.Builder("确定解锁此设备？").apply {
                            negativeTxt = StringUtils.getString(R.string.cancel)
                            setPositiveButton(StringUtils.getString(R.string.sure)) {
                                detail.items.firstOrNull()?.let { first ->
                                    mViewModel.startDrinkingDevice(
                                        first.id,
                                        detail.imei,
                                        detail.categoryCode,
                                    ) {
                                        SToast.showToast(
                                            this@DeviceDetailActivity,
                                            R.string.unlock_success
                                        )
                                    }
                                }
                            }
                        }.build().show(supportFragmentManager)
                    } else {
                        CommonDialog.Builder("确定开锁此设备？").apply {
                            negativeTxt = StringUtils.getString(R.string.cancel)
                            setPositiveButton(StringUtils.getString(R.string.sure)) {
                                mViewModel.deviceOpenCap(detail.imei)
                            }
                        }.build().show(supportFragmentManager)
                    }
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

                13 -> mViewModel.deviceDetail.value?.let { detail ->
                    startActivity(Intent(
                        this@DeviceDetailActivity,
                        DeviceOtherParamsUpdateActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.DeviceParamsUpdateParams.pack(
                                GsonUtils.any2Json(detail.toUpdateParams()),
                            )
                        )
                    })
                }

                14 -> mViewModel.deviceDetail.value?.let { detail ->
                    startNext.launch(
                        Intent(
                            this@DeviceDetailActivity,
                            DeviceMultiChangeActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.DeviceParamsUpdateParams.pack(
                                    GsonUtils.any2Json(detail.toUpdateParams()),
                                    IntentParams.DeviceParamsUpdateParams.typeChangeFloor,
                                    mViewModel.deviceDetail.value?.floorCode
                                )
                            )
                        }
                    )
                }

                15 -> {
                    // 设备转移
                    preTransferDevices()
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
        mBinding.switchDeviceDetailOpen.setOnSwitchClickListener { switch ->
            if (switch.isChecked) {
                CommonDialog.Builder("确定停用此设备？").apply {
                    negativeTxt = StringUtils.getString(R.string.cancel)
                    setPositiveButton(StringUtils.getString(R.string.sure)) {
                        switch.isChecked = false
                    }
                }.build().show(supportFragmentManager)
                true
            } else false
        }

        mBinding.switchDeviceDetailOpen.setOnSwitchClickListener {
            mViewModel.deviceDetail.value?.let { details ->
                mViewModel.switchDevice(!details.soldStateVal)
            }
            true
        }

        // 初始化功能操作区
        val itemW = ScreenUtils.screenWidth / mBinding.glDeviceDetailFunc.columnCount
        val inflater = LayoutInflater.from(this@DeviceDetailActivity)
        mViewModel.deviceDetailFunOperate.forEachIndexed { _, config ->
            ItemDeviceDetailFuncBinding.inflate(inflater).also { childBinding ->
                childBinding.tvDeviceDetailFunc.text = StringUtils.getString(config.title)
                childBinding.root.tag = config.icon
                childBinding.tvDeviceDetailFunc.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    config.icon,
                    0,
                    0
                )
                config.show.observe(this) { show ->
                    if (!show) {
                        mBinding.glDeviceDetailFunc.removeView(childBinding.root)
                    }
                }
                childBinding.root.setOnClickListener(config.onClick)
                mBinding.glDeviceDetailFunc.addView(
                    childBinding.root,
                    ViewGroup.LayoutParams(itemW, ViewGroup.LayoutParams.WRAP_CONTENT)
                )
            }
        }

        mBinding.btnDeviceDetailDelete.setOnClickListener {
            if (true == mViewModel.deviceDetail.value?.needAudit) {
                startActivity(
                    Intent(
                        this@DeviceDetailActivity,
                        DeviceUnbindAuditActivity::class.java
                    ).apply {
                        putExtras(IntentParams.CommonParams.pack(mViewModel.goodsId))
                    }
                )
            } else {
                CommonDialog.Builder(StringUtils.getString(R.string.device_delete_hint)).apply {
                    negativeTxt = StringUtils.getString(R.string.cancel)
                    setPositiveButton(StringUtils.getString(R.string.unBind)) {
                        mViewModel.deviceDelete()
                    }
                }.build().show(supportFragmentManager)
            }
        }

        mBinding.includeDispenserTemperature.tvDispenserItemLimit.setOnClickListener {
            startActivity(Intent(
                this@DeviceDetailActivity, DropperTemperatureActivity::class.java
            ).apply {
                putExtra(
                    "max",
                    "${mViewModel.deviceDetail.value?.deviceAttributeVo?.maxTemperature}"

                )
                putExtra(
                    "min",
                    "${mViewModel.deviceDetail.value?.deviceAttributeVo?.minTemperature}"
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

        mBinding.includeDispenserLaundry.tvDispenserItemLimit.setOnClickListener {
            isDeviceActivateType = 1
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }
        mBinding.includeDispenserRemaining.tvDispenserItemLimit.setOnClickListener {
            isDeviceActivateType = 2
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }

        mBinding.includeCurrentOrder.tvBaseInfoContent.setTextColor(
            ContextCompat.getColor(
                this@DeviceDetailActivity,
                R.color.colorPrimary
            )
        )
        mBinding.includeQueuedOrder.tvBaseInfoContent.setTextColor(
            ContextCompat.getColor(
                this@DeviceDetailActivity,
                R.color.colorPrimary
            )
        )
        // 关联订单
        mBinding.includeCurrentOrder.root.setOnClickListener {
            startActivity(Intent(
                this@DeviceDetailActivity, OrderDetailActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.OrderDetailParams.pack(
                        orderNo = mViewModel.deviceDetail.value?.errorDeviceOrderNo
                    )
                )
            })
        }
        // 排队订单
        mBinding.includeQueuedOrder.root.setOnClickListener {
            startActivity(Intent(
                this@DeviceDetailActivity, OrderDetailActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.OrderDetailParams.pack(
                        orderNo = mViewModel.deviceDetail.value?.queuedOrderNo
                    )
                )
            })
        }
        mBinding.tvDeviceDetailMoreOrder.setOnClickListener {
            startActivity(
                Intent(
                    this@DeviceDetailActivity,
                    OrderManagerActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.OrderManagerParams.pack(
                            1,
                            mViewModel.deviceDetail.value?.id,
                            mViewModel.deviceDetail.value?.name,
                            mViewModel.deviceDetail.value?.imei,
                        )
                    )
                })
        }

        // 功能配置
        mBinding.rvDeviceDetailFuncPrice.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.divide_size8
        )?.let {
            mBinding.rvDeviceDetailFuncPrice.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvDeviceDetailFuncPrice.adapter = mFunAdapter
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestData()
    }
}