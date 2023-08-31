package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceFunConfigurationAddV2ViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.databinding.ActivityDeviceFunConfigurationAddV2Binding
import com.yunshang.haile_manager_android.databinding.ItemDeviceFunConfigurationAddV2Binding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceFunConfigurationAddV2Activity :
    BaseBusinessActivity<ActivityDeviceFunConfigurationAddV2Binding, DeviceFunConfigurationAddV2ViewModel>(
        DeviceFunConfigurationAddV2ViewModel::class.java
    ) {

    override fun initIntent() {
        super.initIntent()
        mViewModel.items.value =
            IntentParams.DeviceFunConfigurationAddV2Params.parseSkuExtAttrDto(intent)
                ?: mutableListOf()
    }

    override fun layoutId(): Int = R.layout.activity_device_fun_configuration_add_v2


    override fun initEvent() {
        super.initEvent()
        mViewModel.items.observe(this) {
            buildConfigureList(it)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.btnDeviceFunConfigureAddV2Add.setOnClickListener {
            mViewModel.items.value?.let { items ->
                items.firstOrNull()?.let { first ->
                    items.add(
                        first.copy(
                            id = "",
                            pulse = 0,
                            unitAmount = "",
                            unitPrice = 0.0,
                            isOn = true,
                            canMerchantEdit = true,
                            isDefault = false,
                        )
                    )
                    mViewModel.items.value = items
                }
            }
        }
        mBinding.btnDeviceFunConfigureAddSave.setOnClickListener {
            mViewModel.items.value?.let {
                setResult(RESULT_OK, Intent().apply {
                    putExtras(
                        IntentParams.DeviceFunConfigurationAddV2Params.pack(
                            IntentParams.DeviceFunConfigurationAddV2Params.parseSkuId(intent),
                            it
                        )
                    )
                })
            }
            finish()
        }
    }

    private fun buildConfigureList(extAttrDtoItems: MutableList<ExtAttrDtoItem>) {
        mBinding.llDeviceFunConfigureList.buildChild<ItemDeviceFunConfigurationAddV2Binding, ExtAttrDtoItem>(
            extAttrDtoItems, start = 1
        ) { index, childBinding, data ->
            childBinding.item = data
            childBinding.ibDeviceFunConfigureDelete.setOnClickListener {
                mViewModel.items.value?.let { list ->
                    if (list.size > index){
                        list.removeAt(index)
                        mViewModel.items.value = list
                    }
                }
            }
        }
    }

    override fun initData() {
    }
}