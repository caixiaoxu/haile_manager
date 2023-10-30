package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DropperTemperatureViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDropperTemperatureBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DropperTemperatureActivity :
    BaseBusinessActivity<ActivityDropperTemperatureBinding, DropperTemperatureViewModel>(
        DropperTemperatureViewModel::class.java,
        BR.vm
    ) {


    override fun layoutId(): Int = R.layout.activity_dropper_temperature

    override fun backBtn(): View = mBinding.barDeviceTemperatureTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mViewModel.imei.postValue(intent.getStringExtra("imei"))
        mViewModel.max.postValue(intent.getStringExtra("max"))
        mViewModel.min.postValue(intent.getStringExtra("min"))
//        var switch = intent.getBooleanExtra("temperatureSwitch", true)
//        mViewModel.temperatureSwitch.postValue(switch)
//        setEditState(switch)
//        mBinding.etMaxTemperature.addTextChangedListener { text ->
//            mViewModel.max.postValue(text.toString())
//        }
//        mBinding.etMinTemperature.addTextChangedListener { text ->
//            mViewModel.min.postValue(text.toString())
//        }
//        mBinding.cbTemper.setOnCheckedChangeListener { _, isChecked ->
//            setEditState(isChecked)
//        }
    }

//    private fun setEditState(isChecked: Boolean) {
//        mViewModel.temperatureSwitch.postValue(isChecked)
//        mBinding.etMinTemperature.isFocusable = isChecked
//        mBinding.etMinTemperature.isFocusableInTouchMode = isChecked
//        mBinding.etMinTemperature.setTextColor(
//            if (isChecked) Color.parseColor("#333333") else Color.parseColor(
//                "#cccccc"
//            )
//        )
//        mBinding.etMaxTemperature.isFocusable = isChecked
//        mBinding.etMaxTemperature.isFocusableInTouchMode = isChecked
//        mBinding.etMaxTemperature.setTextColor(
//            if (isChecked) Color.parseColor("#333333") else Color.parseColor(
//                "#cccccc"
//            )
//        )
//    }

    override fun initData() {
    }
}