package com.shangyun.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.Dimension
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.ActivityDevicePayCodeBinding
import com.shangyun.haile_manager_android.utils.BitmapUtils

class DevicePayCodeActivity : BaseBindingActivity<ActivityDevicePayCodeBinding>() {
    override fun layoutId(): Int = R.layout.activity_device_pay_code

    companion object {
        const val Code = "code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.barDevicePayCodeTitle.getBackBtn().setOnClickListener {
            onBackListener()
        }

        intent.getStringExtra(Code)?.let { code ->
            val wh = DimensionUtils.dip2px(this, 166f)
            BitmapUtils.createQRCodeBitmap(
                code, wh, wh, "UTF-8", "H", "1",
                Color.BLACK, Color.WHITE
            )?.let { bitmap ->
                mBinding.ivDevicePayCode.setImageBitmap(bitmap)
                mBinding.btnDevicePayCodeSave.setOnClickListener {
                    if (BitmapUtils.saveBitmap(this@DevicePayCodeActivity, "payCode", bitmap)) {
                        SToast.showToast(this@DevicePayCodeActivity, "保存成功")
                    } else {
                        SToast.showToast(this@DevicePayCodeActivity, "保存失败")
                    }
                }
            }
        }
    }
}