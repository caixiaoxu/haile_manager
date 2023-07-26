package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceBatchUpdateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.Spu
import com.yunshang.haile_manager_android.databinding.ActivityDeviceBatchUpdateBinding
import com.yunshang.haile_manager_android.databinding.ItemSelectedDeviceFuncationConfigurationBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceBatchUpdateActivity :
    BaseBusinessActivity<ActivityDeviceBatchUpdateBinding, DeviceBatchUpdateViewModel>(
        DeviceBatchUpdateViewModel::class.java, BR.vm
    ) {

    // 跳转回调界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                // 搜索店铺
                IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                    it.data?.let { intent ->
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                GsonUtils.json2List(json, SearchSelectParam::class.java)
                                    ?.let { selected ->
                                        mViewModel.selectDepartments.value = selected
                                    }
                            }
                    }
                }
                // 搜索设备型号
                IntentParams.SearchSelectTypeParam.DeviceModelResultCode -> {
                    it.data?.let { intent ->
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                GsonUtils.json2List(json, SearchSelectParam::class.java)
                                    ?.let { selected ->
                                        mViewModel.selectDeviceModel.value =
                                            selected.firstOrNull()
                                    }
                            }
                    }
                }
                // 配置
                IntentParams.DeviceFunctionConfigurationParams.ResultCode -> {
                    it.data?.let { intent ->
                        IntentParams.DeviceFunctionConfigurationParams.parseSkuFuncConfiguration(
                            intent
                        )?.let { configures ->
                            mViewModel.createDeviceFunConfigure.value = configures
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_batch_update

    override fun backBtn(): View = mBinding.barDeviceBatchUpdateTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        // 设备类型
        mViewModel.categoryList.observe(this) {
            showDeviceCategoryDialog(it)
        }

        // 功能配置
        mViewModel.createDeviceFunConfigure.observe(this) {
            it?.let { list ->
                mBinding.llDeviceBatchUpdateSelectFunConfiguration.removeAllViews()
                val mtb = DimensionUtils.dip2px(this@DeviceBatchUpdateActivity, 12f)
                list.forEachIndexed { index, config ->
                    val selectFuncConfigItem =
                        LayoutInflater.from(this@DeviceBatchUpdateActivity)
                            .inflate(
                                R.layout.item_selected_device_funcation_configuration,
                                null,
                                false
                            )
                    val mFuncConfigBinding =
                        DataBindingUtil.bind<ItemSelectedDeviceFuncationConfigurationBinding>(
                            selectFuncConfigItem
                        )
                    mFuncConfigBinding?.let {
                        val categoryCode = mViewModel.selectCategory.value?.code
                        mFuncConfigBinding.item = config
                        mFuncConfigBinding.isDryer =
                            DeviceCategory.isDryerOrHair(categoryCode)
                        mFuncConfigBinding.deviceCommunicationType =
                            mViewModel.selectDeviceModel.value?.origin?.let { origin ->
                                if (origin is Spu) {
                                    origin.communicationType
                                } else -1
                            } ?: -1
                        mFuncConfigBinding.tvSelectFuncConfigurationFeature.visibility =
                            if (DeviceCategory.isHair(categoryCode)) View.GONE else View.VISIBLE
                        mBinding.llDeviceBatchUpdateSelectFunConfiguration.addView(
                            mFuncConfigBinding.root,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, if (0 == index) mtb else 0, 0, mtb)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 所属门店 多选
        mBinding.itemDeviceBatchUpdateDepartment.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceBatchUpdateActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = true,
                            moreSelect = true,
                            selectArr = mViewModel.selectDepartments.value?.map { it.id }
                                ?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }

        // 设备类型 单选
        mBinding.itemDeviceBatchUpdateCategory.onSelectedEvent = {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mViewModel.requestDeviceCategory()
        }

        // 设备型号
        mBinding.itemDeviceBatchUpdateModel.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceBatchUpdateActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeDeviceModel,
                            mViewModel.selectCategory.value?.id ?: -1,
                            shopIdList = mViewModel.selectDepartments.value?.map { it.id }
                                ?.toIntArray(),
                            mustSelect = false
                        )
                    )
                }
            )
        }

        // 功能配置
        mBinding.itemDeviceBatchUpdateFunConfigure.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceBatchUpdateActivity,
                    DeviceFunctionConfigurationActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.DeviceFunctionConfigurationParams.pack(
                            spuId = mViewModel.selectDeviceModel.value?.id,
                            categoryCode = mViewModel.selectCategory.value?.code,
                            communicationType = mViewModel.selectDeviceModel.value?.origin?.let { origin ->
                                if (origin is Spu) {
                                    origin.communicationType
                                } else -1
                            } ?: -1,
                            oldFuncConfiguration = mViewModel.createDeviceFunConfigure.value
                        )
                    )
                }
            )
        }
        mViewModel.jump.observe(this){
            finish()
        }
    }

    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceCategoryDialog(categoryEntities: List<CategoryEntity>) {
        CommonBottomSheetDialog.Builder(
            getString(R.string.device_category),
            categoryEntities
        ).apply {
            mustSelect = false
            onValueSureListener =
                object :
                    CommonBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                    override fun onValue(data: CategoryEntity?) {
                        mViewModel.selectCategory.value = data
                        mViewModel.selectDeviceModel.value = null
                    }
                }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
    }
}