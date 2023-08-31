package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatRadioButton
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceStartViewModel
import com.yunshang.haile_manager_android.data.arguments.DeviceConfigSelectParams
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.databinding.ActivityDeviceStartBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceStartActivity :
    BaseBusinessActivity<ActivityDeviceStartBinding, DeviceStartViewModel>(
        DeviceStartViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val Imei = "imei"
        const val Items = "items"
    }

    override fun layoutId(): Int = R.layout.activity_device_start

    override fun backBtn(): View = mBinding.barDeviceStartTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.imei = intent.getStringExtra(Imei)
        mViewModel.categoryCode = intent.getStringExtra(DeviceCategory.CategoryCode)
        intent.getStringExtra(Items)?.let {
            GsonUtils.json2List(it, SkuFunConfigurationV2Param::class.java)?.let { list ->
                mViewModel.items = list.filter { item -> 1 == item.soldState }.map { item ->
                    if (DeviceCategory.isWashingOrShoes(mViewModel.categoryCode)) {
                        val attr = item.extAttrDto.items.firstOrNull()
                        val times = attr?.unitAmount?.let { unit -> listOf(unit) } ?: listOf()
                        DeviceConfigSelectParams(
                            item.id,
                            "${item.name} ${attr?.getTitle() ?: ""} ${attr?.unitPriceVal?.let { price -> "${price}元" } ?: ""}",
                            times)
                    } else {
                        DeviceConfigSelectParams(
                            item.id,
                            item.name,
                            item.extAttrDto.items.map { ext -> ext.unitAmount })
                    }
                }
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.selectItem.observe(this) {
            it?.let { item ->
                if (DeviceCategory.isDryerOrHair(mViewModel.categoryCode)) {
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
                                                mViewModel.selectTime.value = time
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

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.tvDeviceStartModel.hint =
            StringUtils.getString(R.string.please_select) + DeviceCategory.deviceCategoryName(
                mViewModel.categoryCode
            ) + StringUtils.getString(R.string.model)
        mBinding.tvDeviceStartModel.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                DeviceCategory.deviceCategoryName(mViewModel.categoryCode) + StringUtils.getString(R.string.model),
                mViewModel.items
            ).apply {
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<DeviceConfigSelectParams> {
                        override fun onValue(data: DeviceConfigSelectParams?) {
                            mViewModel.selectItem.value = data
                            if (!DeviceCategory.isDryerOrHair(mViewModel.categoryCode)) {
                                mViewModel.selectTime.value = data?.times?.firstOrNull()
                            }
                        }
                    }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
    }
}