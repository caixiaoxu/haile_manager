package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import com.king.camera.scan.CameraScan
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceCreateV2ViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceCreateV2Binding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.WeChatQRCodeScanActivity
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep1Fragment
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber

/**
 * 设备增加新流程修改，废弃旧流程DeviceCreateActivity
 */
class DeviceCreateV2Activity :
    BaseBusinessActivity<ActivityDeviceCreateV2Binding, DeviceCreateV2ViewModel>(
        DeviceCreateV2ViewModel::class.java, BR.vm
    ) {

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
                    StringUtils.getPayImeiCode(it)?.let { code ->
                        mViewModel.payCode.value = code
                        mViewModel.imeiCode.value = code
                        mViewModel.codeStr.value = it
                    } ?: run {
                        val payCode = StringUtils.getPayCode(it)
                        if (null != payCode) {
                            mViewModel.payCode.value = payCode
                        } else if (StringUtils.isImeiCode(it)) {
                            mViewModel.imeiCode.value = it
                        } else
                            SToast.showToast(this, R.string.scan_code_error)
                    }
                } ?: SToast.showToast(this, R.string.imei_code_error)
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

        mViewModel.step.observe(this) {
            jumpPage(it)
        }

        mViewModel.imeiCode.observe(this) {
            // 如果符合规则就查询型号
            // binding双向绑定，会调用两次
            if (StringUtils.isImeiCode(it)) {
                clearImeiEditFocus()
                mViewModel.requestModelOfImei(it)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnDeviceCreateNextOrSave.setOnClickListener {
            if (mViewModel.step.value!! < (mViewModel.deviceCreateStepFragments.size - 1)) {
                mViewModel.step.value = mViewModel.step.value!! + 1
            } else {
                requestMultiplePermission.launch(
                    SystemPermissionHelper.cameraPermissions()
                        .plus(SystemPermissionHelper.readWritePermissions())
                )
            }
        }
    }

    private fun clearImeiEditFocus() {
        (mViewModel.deviceCreateStepFragments[0] as DeviceCreateStep1Fragment).clearImeiFocus()
    }

    private fun jumpPage(step: Int) {
        var stepTemp = step
        // 如果查到绑定设备，跳过模式选择界面
        if (1 == step && 0 < mViewModel.spuId && 0 < mViewModel.categoryId) {
            stepTemp = 2
        }
        refreshTitleBar(stepTemp)
        refreshFragmentStep(stepTemp)
    }

    private fun refreshTitleBar(step: Int) {
        when (step) {
            0 -> mBinding.barDeviceCreateV2Title.getRightArea().run {
                removeAllViews()
                setPadding(0, 0, DimensionUtils.dip2px(this@DeviceCreateV2Activity, 16f), 0)
                addView(AppCompatTextView(this@DeviceCreateV2Activity).apply {
                    setText(R.string.scan_input)
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    val pH = DimensionUtils.dip2px(this@DeviceCreateV2Activity, 12f)
                    val pV = DimensionUtils.dip2px(this@DeviceCreateV2Activity, 4f)
                    setPadding(pH, pV, pH, pV)
                    setCompoundDrawablesWithIntrinsicBounds(
                        R.mipmap.icon_device_create_scan, 0, 0, 0
                    )
                    setBackgroundResource(R.drawable.shape_sf0a258_r14)
                    compoundDrawablePadding = pV
                    setOnClickListener {
                        requestMultiplePermission.launch(
                            SystemPermissionHelper.cameraPermissions()
                                .plus(SystemPermissionHelper.readWritePermissions())
                        )
                    }
                }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            1, 2 -> mBinding.barDeviceCreateV2Title.getRightArea().run {
                removeAllViews()
            }
        }
    }

    private fun refreshFragmentStep(step: Int) {
        if (0 <= step && step < mViewModel.deviceCreateStepFragments.size) {
            val curFragment = mViewModel.deviceCreateStepFragments[step]
            val name = curFragment.javaClass.simpleName
            if (null == supportFragmentManager.findFragmentByTag(name)) {
                try {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fl_device_create_parent, curFragment, name)
                        .addToBackStack(name)
                        .commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackListener() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            mViewModel.step.value = mViewModel.step.value!! - 1
            supportFragmentManager.popBackStack()
        } else {
            super.onBackListener()
        }
    }

    override fun initData() {
    }
}