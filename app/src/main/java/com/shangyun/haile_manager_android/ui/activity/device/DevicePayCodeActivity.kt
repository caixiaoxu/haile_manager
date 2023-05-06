package com.shangyun.haile_manager_android.ui.activity.device

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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

    // 权限
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                // 授权权限成功
                save()
            } else {
                // 授权失败
                SToast.showToast(this, "需要授权才可保存")
            }
        }

    private var bitmap: Bitmap? = null

    override fun backBtn(): View = mBinding.barDevicePayCodeTitle.getBackBtn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE

        intent.getStringExtra(Code)?.let { code ->
            val wh = DimensionUtils.dip2px(this, 166f)
            // 生成二维码
            BitmapUtils.createQRCodeBitmap(
                code, wh, wh, "UTF-8", "H", "1",
                Color.BLACK, Color.WHITE
            )?.let { bitmap ->
                this.bitmap = bitmap
                mBinding.ivDevicePayCode.setImageBitmap(bitmap)
                //点击保存
                mBinding.btnDevicePayCodeSave.setOnClickListener {
                    requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    /**
     * 保存
     */
    private fun save() {
        bitmap?.let {
            if (BitmapUtils.saveBitmapToGallery(this, "payCode.jpg", it)
            ) {
                SToast.showToast(this, "保存成功")
            } else {
                SToast.showToast(this, "保存失败")
            }
        }
    }
}