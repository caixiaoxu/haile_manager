package com.yunshang.haile_manager_android.ui.activity.device

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.SoftKeyboardUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceFunConfigurationV2ViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.databinding.ActivityDeviceFunConfigurationV2Binding
import com.yunshang.haile_manager_android.databinding.ItemDeviceFunConfigureationV2Binding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceFunConfigurationV2Activity :
    BaseBusinessActivity<ActivityDeviceFunConfigurationV2Binding, DeviceFunConfigurationV2ViewModel>(
        DeviceFunConfigurationV2ViewModel::class.java
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceFunConfigureationV2Binding, SkuFunConfigurationV2Param>(
            R.layout.item_device_fun_configureation_v2, BR.item
        ) { mItemBinding, pos, item ->

        }
    }

    override fun layoutId(): Int = R.layout.activity_device_fun_configuration_v2

    override fun initIntent() {
        super.initIntent()
        mViewModel.spuId = IntentParams.DeviceFunConfigurationV2Params.parseSpuId(intent)
        mViewModel.categoryCode.value = IntentParams.DeviceParams.parseCategoryCode(intent)
        mViewModel.spuExtAttrDto =
            IntentParams.DeviceFunConfigurationV2Params.parseExtAttrDto(intent)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.configureList.observe(this) {
            it?.let {
                mAdapter.refreshList(it, true)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.itemDeviceFunConfigurationPriceModel.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.pricing_manner), mViewModel.priceModelList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.priceModel.value = data
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }
        mBinding.itemDeviceFunConfigurationCalculateModel.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.pricing_manner), mViewModel.calculateModelList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.let {
                            mViewModel.calculateModel.value = data
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        mBinding.rvDeviceFunConfigurationList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.divder_f7f7f7_size8
        )?.let {
            mBinding.rvDeviceFunConfigurationList.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvDeviceFunConfigurationList.adapter = mAdapter
//        // 保存
//        mBinding.btnDeviceCreateSubmit.setOnClickListener {
//            SoftKeyboardUtils.hideShowKeyboard(it)
//            mViewModel.save()
//        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}