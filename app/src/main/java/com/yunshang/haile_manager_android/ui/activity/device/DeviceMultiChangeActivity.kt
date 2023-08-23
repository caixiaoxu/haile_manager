package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceMultiChangeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber

class DeviceMultiChangeActivity :
    BaseBusinessActivity<ActivityDeviceMultiChangeBinding, DeviceMultiChangeViewModel>(
        DeviceMultiChangeViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val GoodId = "goodId"
        const val ResultCode = 0x70001
        const val ResultData = "ResultData"
    }

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
                when (mViewModel.type.value) {
                    DeviceMultiChangeViewModel.typeChangePayCode -> {
                        CameraScan.parseScanResult(result.data)?.let {
                            Timber.i("扫码:$it")
                            val payCode = StringUtils.getPayCode(it)
                            payCode?.let { code ->
                                mViewModel.content.value = code
                                mViewModel.originCode = it
                            } ?: SToast.showToast(this, R.string.pay_code_error)
                        }
                    }
                    DeviceMultiChangeViewModel.typeChangeModel -> {
                        CameraScan.parseScanResult(result.data)?.let {
                            Timber.i("扫码:$it")
                            mViewModel.content.value = it
                        } ?: SToast.showToast(this, R.string.imei_code_error)
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

    override fun layoutId(): Int = R.layout.activity_device_multi_change

    override fun backBtn(): View = mBinding.barDeviceMultiChangeTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodId = intent.getIntExtra(GoodId, -1)
        mViewModel.type.value =
            intent.getIntExtra(DeviceMultiChangeViewModel.Type, 0)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.jump.observe(this) {
            setResult(ResultCode, Intent().apply {
                putExtra(DeviceMultiChangeViewModel.Type, mViewModel.type.value)
                putExtra(ResultData, mViewModel.content.value)
            })
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barDeviceMultiChangeTitle.getTitle().text =
            mViewModel.titles[mViewModel.type.value!!]

        mBinding.btnDeviceMultiChangeScan.setOnClickListener {
            requestMultiplePermission.launch(
                SystemPermissionHelper.cameraPermissions()
                    .plus(SystemPermissionHelper.readWritePermissions())
            )
        }
    }

    override fun initData() {
    }
}