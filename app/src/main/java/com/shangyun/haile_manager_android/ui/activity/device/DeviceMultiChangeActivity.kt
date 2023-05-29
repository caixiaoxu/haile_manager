package com.shangyun.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDeviceMultiChangeBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.CustomCaptureActivity
import com.shangyun.haile_manager_android.utils.StringUtils

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

    // 相机启动器
    private val codeLauncher = registerForActivityResult(ScanContract()) { result ->
        when (mViewModel.type.value) {
            DeviceMultiChangeViewModel.typeChangePayCode -> {
                result.contents?.let {
                    val payCode = StringUtils.getPayCode(it)
                    payCode?.let { code ->
                        mViewModel.content.value = code
                    } ?: SToast.showToast(this, R.string.pay_code_error)
                }
            }
            DeviceMultiChangeViewModel.typeChangeModel -> {
                result.contents?.let {
                    mViewModel.content.value = it
                } ?: SToast.showToast(this, R.string.imei_code_error)
            }
        }
    }

    private val scanOptions: ScanOptions by lazy {
        ScanOptions().apply {
            captureActivity = CustomCaptureActivity::class.java
            setPrompt("请对准二维码")//提示语
            setOrientationLocked(true)
            setCameraId(0) // 选择摄像头
            setBeepEnabled(true) // 开启声音
        }
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
            codeLauncher.launch(scanOptions)
        }
    }

    override fun initData() {
    }
}