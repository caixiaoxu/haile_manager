package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceCreateV2ViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.databinding.ActivityDeviceCreateV2Binding
import com.yunshang.haile_manager_android.databinding.ItemSelectFunConfigureAttrItemV2Binding
import com.yunshang.haile_manager_android.databinding.ItemSelectFunConfigureV2Binding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
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
                    val originCodeTrim = it.trim()
                    if (isAttrImei) {
                        Timber.i("IMEI:$it")
                        if (StringUtils.isImeiCode(originCodeTrim)) mViewModel.washImeiCode.value = originCodeTrim
                        else SToast.showToast(this, R.string.imei_code_error1)
                    } else {
                        mViewModel.codeStr = originCodeTrim
                        StringUtils.getPayImeiCode(originCodeTrim)?.let { code ->
                            mViewModel.payCode.value = code
                            mViewModel.imeiCode.value = code
                        } ?: run {
                            val payCode = StringUtils.getPayCode(originCodeTrim)
                            if (null != payCode) {
                                mViewModel.payCode.value = payCode
                            } else if (StringUtils.isImeiCode(originCodeTrim)) {
                                mViewModel.imeiCode.value = originCodeTrim
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
                IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                    result.data?.let {intent->
                        IntentParams.ShopPositionSelectorParams.parseSelectList(intent)?.firstOrNull()
                            ?.let {
                                mViewModel.createDeviceShop.value = it
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
                IntentParams.DeviceFunConfigurationV2Params.ResultCode -> {
                    result.data?.let { intent ->
                        IntentParams.DeviceFunConfigurationV2Params.parseSkuExtAttrDto(
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

    private val mFunAdapter by lazy {
        CommonRecyclerAdapter<ItemSelectFunConfigureV2Binding, SkuFunConfigurationV2Param>(
            R.layout.item_select_fun_configure_v2, BR.item
        ) { mItemBinding, pos, item ->

            item.extAttrDto.items.filter { attr -> attr.isEnabled }.let {
                val isPulseDevice = DeviceCategory.isPulseDevice(mViewModel.deviceCommunicationType)
                mItemBinding?.llSelectFunConfigureAttrs?.buildChild<ItemSelectFunConfigureAttrItemV2Binding, ExtAttrDtoItem>(
                    it
                ) { index, childBinding, data ->
                    childBinding.type = 0
                    childBinding.title =
                        if (0 == index) com.lsy.framelib.utils.StringUtils.getString(R.string.price_configure) + "：" else ""
                    childBinding.value =
                        "${data.unitPriceVal}元/${data.getTitle()}${if (isPulseDevice) "/${data.pulse}个" else ""}"
                    childBinding.isDefault = data.isDefault
                }
            }
        }
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
            mFunAdapter.refreshList(it, true)
        }

        // 跳转
        mViewModel.jump.observe(this) {
            finish()
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
                this@DeviceCreateV2Activity,
                ShopPositionSelectorActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.ShopPositionSelectorParams.pack(false)
                )
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

        //楼层
        mBinding.itemDeviceCreateFloor.mTailView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.common_txt_color
            )
        )

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
                            mViewModel.extAttrDtoJson.value,
                            mViewModel.createDeviceFunConfigure.value
                        )
                    )
                }
            )
        }

        // 功能配置
        mBinding.rvDeviceCreateSelectFunConfiguration.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.divide_size8
        )?.let {
            mBinding.rvDeviceCreateSelectFunConfiguration.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvDeviceCreateSelectFunConfiguration.adapter = mFunAdapter
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