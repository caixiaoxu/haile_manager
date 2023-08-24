package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceCreateV2ViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceCreateV2Binding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

/**
 * 设备增加新流程修改，废弃旧流程DeviceCreateActivity
 */
class DeviceCreateV2Activity :
    BaseBusinessActivity<ActivityDeviceCreateV2Binding, DeviceCreateV2ViewModel>(
        DeviceCreateV2ViewModel::class.java,BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_create_v2

    override fun backBtn(): View = mBinding.barDeviceCreateV2Title.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.step.observe(this) {
            refreshTitleBar(it)
            refreshFragmentStep(it)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnDeviceCreateNextOrSave.setOnClickListener {
            if (mViewModel.step.value!! < (mViewModel.deviceCreateStepFragments.size - 1)) {
                mViewModel.step.value = mViewModel.step.value!! + 1
            } else {

            }
        }
    }

    private fun refreshTitleBar(step: Int) {
        when (step) {
            0 -> mBinding.barDeviceCreateV2Title.getRightArea().run {
                removeAllViews()

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
                    compoundDrawablePadding = pV
                    setOnClickListener {
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
            val name = curFragment.javaClass.name
            supportFragmentManager.beginTransaction()
                .add(R.id.fl_device_create_parent, curFragment, name)
                .addToBackStack(name)
                .commit()
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