package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceAdvancedSettingViewModel
import com.yunshang.haile_manager_android.data.entities.DeviceAdvancedEntity
import com.yunshang.haile_manager_android.data.entities.DeviceAdvancedOptionEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceAdvancedSettingBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceAdvancedSettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceAdvancedSettingActivity :
    BaseBusinessActivity<ActivityDeviceAdvancedSettingBinding, DeviceAdvancedSettingViewModel>(
        DeviceAdvancedSettingViewModel::class.java,
        BR.vm
    ) {

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
                            override fun onValue(data: DeviceAdvancedOptionEntity?) {
                                item.input = data?.name ?:""
                                item.inputValue = data?.value ?:""
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

    override fun backBtn(): View = mBinding.barDeviceAdvancedSettingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.goodId = intent.getIntExtra(GoodId, -1)
        mViewModel.functionId = intent.getIntExtra(FunctionId, -1)
        mViewModel.attrs.value = intent.getStringExtra(Attrs)
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.attrList.observe(this) {
            it?.let { list ->
                mAdapter.refreshList(list, true)
            }
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.barDeviceAdvancedSettingTitle.getTitle().text = intent.getStringExtra(FunctionName)
        mBinding.rvDeviceAdvancedSetting.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceAdvancedSetting.adapter = mAdapter
    }

    override fun initData() {
    }
}