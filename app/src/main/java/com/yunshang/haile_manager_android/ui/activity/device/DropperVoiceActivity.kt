package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceDetailModel
import com.yunshang.haile_manager_android.business.vm.DeviceMultiChangeViewModel
import com.yunshang.haile_manager_android.business.vm.DropperVoiceViewModel
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.Item
import com.yunshang.haile_manager_android.databinding.ActivityDeviceDetailBinding
import com.yunshang.haile_manager_android.databinding.ActivityDropperVoiceBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceDetailFuncPriceBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.Date

class DropperVoiceActivity :
    BaseBusinessActivity<ActivityDropperVoiceBinding, DropperVoiceViewModel>(
        DropperVoiceViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val Deviceimei = "imei"
    }

    var imei: String = ""
    var preventDisturbStartTime: String = ""
    var preventDisturbEndTime: String = ""

    override fun layoutId(): Int = R.layout.activity_dropper_voice

    override fun backBtn(): View = mBinding.barDeviceVoiceTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
    }

    override fun initEvent() {
        super.initEvent()

        imei = intent.getStringExtra(Deviceimei)!!

        var process = intent.getIntExtra("process", 0)
        var preventDisturbSwitch = intent.getBooleanExtra("preventDisturbSwitch", true)
        var voiceBroadcastStatus = intent.getBooleanExtra("voiceBroadcastStatus", true)
        preventDisturbStartTime = intent.getStringExtra("preventDisturbStartTime")!!
        preventDisturbEndTime = intent.getStringExtra("preventDisturbEndTime")!!
        mBinding.cbTime.isChecked = preventDisturbSwitch
        mBinding.cbVoice.isChecked = voiceBroadcastStatus
        mBinding.sbVoice.progress = process
        mBinding.tvVoice.text = "${process}%"
        if (preventDisturbStartTime.isNotEmpty() && preventDisturbEndTime.isNotEmpty()) {
            mBinding.tvSelectTime.text = "${preventDisturbStartTime}~${preventDisturbEndTime}"
            mBinding.tvSelectTime.setTextColor(Color.parseColor("#333333"))
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.sbVoice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mBinding.tvVoice.text = "${p1}%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {//刚开始触摸的时候
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {//结束触摸
            }
        })
        mBinding.tvSelectTime.setOnClickListener {
            val dailog = DateSelectorDialog.Builder().apply {
                selectModel = 1
                showModel = 4
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        preventDisturbStartTime = DateTimeUtils.formatDateTime(date1, "HH:mm")
                        preventDisturbEndTime = DateTimeUtils.formatDateTime(date2, "HH:mm")
                        mBinding.tvSelectTime.text =
                            preventDisturbStartTime + "~" + preventDisturbEndTime
                        mBinding.tvSelectTime.setTextColor(Color.parseColor("#333333"))
                    }
                }
            }.build()
            dailog.show(supportFragmentManager)
        }

        mBinding.btnDeviceVoiceSubmit.setOnClickListener {
            mViewModel.submit(
                imei,
                mBinding.sbVoice.progress,
                mBinding.cbVoice.isChecked,
                mBinding.cbTime.isChecked,
                preventDisturbStartTime,
                preventDisturbEndTime
            )
        }


    }

    override fun initData() {

    }
}