package com.shangyun.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatRadioButton
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceStartViewModel
import com.shangyun.haile_manager_android.data.arguments.DeviceCategory
import com.shangyun.haile_manager_android.data.arguments.DeviceConfigSelectParams
import com.shangyun.haile_manager_android.data.entities.ExtAttrBean
import com.shangyun.haile_manager_android.data.entities.Item
import com.shangyun.haile_manager_android.databinding.ActivityDeviceStartBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceStartActivity :
    BaseBusinessActivity<ActivityDeviceStartBinding, DeviceStartViewModel>() {

    private val mDeviceStartViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceStartViewModel::class.java]
    }

    companion object {
        const val Imei = "imei"
        const val Items = "items"
    }

    override fun layoutId(): Int = R.layout.activity_device_start

    override fun getVM(): DeviceStartViewModel = mDeviceStartViewModel

    override fun backBtn(): View? = mBinding.barDeviceStartTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mDeviceStartViewModel.imei = intent.getStringExtra(Imei)
        mDeviceStartViewModel.categoryCode = intent.getStringExtra(DeviceCategory.CategoryCode)
        intent.getStringExtra(Items)?.let {
            GsonUtils.json2List(it, Item::class.java)?.let { list ->
                mDeviceStartViewModel.items = list.map { item ->
                    if (DeviceCategory.isDryer(mDeviceStartViewModel.categoryCode)) {
                        val times =
                            GsonUtils.json2List(item.extAttr, ExtAttrBean::class.java)?.map { ext ->
                                ext.minutes
                            }
                        DeviceConfigSelectParams(item.id, item.name, times ?: arrayListOf())
                    } else {
                        DeviceConfigSelectParams(
                            item.id,
                            "${item.name}  ${item.unit}分钟 ${item.price}元",
                            try {
                                arrayListOf(item.unit.toInt())
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                                arrayListOf()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        mDeviceStartViewModel.selectItem.observe(this) {
            it?.let { item ->
                if (DeviceCategory.isDryer(mDeviceStartViewModel.categoryCode)) {
                    mBinding.rgDeviceStartTimeList.removeAllViews()
                    val timeW = DimensionUtils.dip2px(dipValue = 70f)
                    val timeH = DimensionUtils.dip2px(dipValue = 28f)
                    val mR = DimensionUtils.dip2px(dipValue = 8f)
                    item.times.forEachIndexed { index, time ->
                        mBinding.rgDeviceStartTimeList.addView(
                            LayoutInflater.from(this@DeviceStartActivity)
                                .inflate(R.layout.item_device_start, null, false).apply {
                                    id = index
                                    (this as AppCompatRadioButton).run {
                                        text = "${time}分钟"
                                        setOnCheckedChangeListener { _, isChecked ->
                                            if (isChecked) {
                                                mDeviceStartViewModel.selectTime.value = time
                                            }
                                        }
                                    }
                                },
                            LinearLayout.LayoutParams(timeW, timeH).apply {
                                setMargins(if (index > 0) mR else 0, 0, 0, 0)
                            }
                        )
                    }
                    mBinding.scrollDeviceStartTimeList.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.tvDeviceStartModel.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.wash_model),
                mDeviceStartViewModel.items
            ).apply {
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<DeviceConfigSelectParams> {
                        override fun onValue(data: DeviceConfigSelectParams) {
                            mDeviceStartViewModel.selectItem.value = data
                            if (!DeviceCategory.isDryer(mDeviceStartViewModel.categoryCode)){
                                mDeviceStartViewModel.selectTime.value = data.times[0]
                            }
                        }
                    }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
        mBinding.vm = mDeviceStartViewModel
    }
}