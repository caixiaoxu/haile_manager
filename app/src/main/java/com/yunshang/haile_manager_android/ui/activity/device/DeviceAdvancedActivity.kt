package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.lsy.framelib.ui.base.activity.BaseActivity
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityDeviceAdvancedBinding

class DeviceAdvancedActivity : BaseActivity() {
    private val mBinding: ActivityDeviceAdvancedBinding by lazy {
        ActivityDeviceAdvancedBinding.inflate(layoutInflater)
    }

    override fun backBtn(): View =
        mBinding.barDeviceAdvancedTitle.getBackBtn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        window.statusBarColor = Color.WHITE

        IntentParams.DeviceAdvanceParams.parseAdvancedList(intent)?.let { values ->
            mBinding.llDeviceAdvancedParams.removeAllViews()
            values.forEach { value ->
                mBinding.llDeviceAdvancedParams.addView(
                    LayoutInflater.from(this@DeviceAdvancedActivity)
                        .inflate(R.layout.item_device_advanced_params, null, false).apply {
                            (this as AppCompatTextView).text = value.functionName
                            setOnClickListener {
                                startActivity(
                                    Intent(
                                        this@DeviceAdvancedActivity,
                                        DeviceAdvancedSettingActivity::class.java
                                    ).apply {
                                        putExtra(
                                            DeviceAdvancedSettingActivity.FunctionId,
                                            value.id
                                        )
                                        putExtra(
                                            DeviceAdvancedSettingActivity.FunctionName,
                                            value.functionName
                                        )
                                        putExtra(
                                            DeviceAdvancedSettingActivity.Attrs,
                                            value.extraAttr
                                        )
                                        putExtras(intent)
                                    }
                                )
                            }
                        },
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }
}