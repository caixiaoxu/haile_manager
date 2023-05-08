package com.shangyun.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DeviceAdvancedSettingViewModel
import com.shangyun.haile_manager_android.data.entities.DeviceAdvancedEntity
import com.shangyun.haile_manager_android.data.entities.DeviceAdvancedOptionEntity
import com.shangyun.haile_manager_android.databinding.ActivityDeviceAdvancedSettingBinding
import com.shangyun.haile_manager_android.databinding.ItemDeviceAdvancedSettingBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceAdvancedSettingActivity :
    BaseBusinessActivity<ActivityDeviceAdvancedSettingBinding, DeviceAdvancedSettingViewModel>() {

    private val mDeviceAdvancedSettingViewModel by lazy {
        getActivityViewModelProvider(this)[DeviceAdvancedSettingViewModel::class.java]
    }

    companion object {
        const val GoodId = "goodId"
        const val FunctionId = "functionId"
        const val FunctionName = "FunctionName"
        const val Attrs = "attrs"
    }

    private val mAdapter: CommonRecyclerAdapter<ItemDeviceAdvancedSettingBinding, DeviceAdvancedEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_device_advanced_setting,
            BR.item
        ) { mItemBinding, pos, item ->
            if ("text" != item.type) {
                mItemBinding?.etDeviceAdvancedSettingValue?.setOnClickListener {
                    CommonBottomSheetDialog.Builder(
                        item.name, item.options
                    ).apply {
                        onValueSureListener = object :
                            CommonBottomSheetDialog.OnValueSureListener<DeviceAdvancedOptionEntity> {
                            override fun onValue(data: DeviceAdvancedOptionEntity) {
                                item.input = data.name
                                item.inputValue = data.value
                                mAdapter.notifyItemChanged(pos)
                            }
                        }
                    }.build().show(supportFragmentManager)
                }
            } else {
                mItemBinding?.etDeviceAdvancedSettingValue?.setOnClickListener(null)
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_advanced_setting

    override fun getVM(): DeviceAdvancedSettingViewModel = mDeviceAdvancedSettingViewModel

    override fun backBtn(): View = mBinding.barDeviceAdvancedSettingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mDeviceAdvancedSettingViewModel.goodId = intent.getIntExtra(GoodId, -1)
        mDeviceAdvancedSettingViewModel.functionId = intent.getIntExtra(FunctionId, -1)
        mDeviceAdvancedSettingViewModel.attrs.value = intent.getStringExtra(Attrs)
    }

    override fun initEvent() {
        super.initEvent()
        mDeviceAdvancedSettingViewModel.attrList.observe(this) {
            it?.let { list ->
                mAdapter.refreshList(list, true)
            }
        }
        mDeviceAdvancedSettingViewModel.jump.observe(this){
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barDeviceAdvancedSettingTitle.getTitle().text= intent.getStringExtra(FunctionName)
        mBinding.rvDeviceAdvancedSetting.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceAdvancedSetting.adapter = mAdapter
    }

    override fun initData() {
        mBinding.vm = mDeviceAdvancedSettingViewModel
    }
}