package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceBatchUpdateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.data.entities.Spu
import com.yunshang.haile_manager_android.databinding.ActivityDeviceBatchUpdateBinding
import com.yunshang.haile_manager_android.databinding.ItemSelectFunConfigureAttrItemV2Binding
import com.yunshang.haile_manager_android.databinding.ItemSelectFunConfigureV2Binding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class DeviceBatchUpdateActivity :
    BaseBusinessActivity<ActivityDeviceBatchUpdateBinding, DeviceBatchUpdateViewModel>(
        DeviceBatchUpdateViewModel::class.java, BR.vm
    ) {

    private val mFunAdapter by lazy {
        CommonRecyclerAdapter<ItemSelectFunConfigureV2Binding, SkuFunConfigurationV2Param>(
            R.layout.item_select_fun_configure_v2, BR.item
        ) { mItemBinding, pos, item ->

            item.extAttrDto.items.filter { attr -> attr.isEnabled }.let {
                // 是否是脉冲设备
                val isPulseDevice =
                    DeviceCategory.isPulseDevice(
                        GsonUtils.json2Class(
                            mViewModel.selectDeviceModel.value?.origin,
                            Spu::class.java
                        )?.communicationType
                    )
                mItemBinding?.llSelectFunConfigureAttrs?.buildChild<ItemSelectFunConfigureAttrItemV2Binding, ExtAttrDtoItem>(
                    it
                ) { index, childBinding, data ->
                    childBinding.type = 0
                    childBinding.title =
                        if (0 == index) StringUtils.getString(R.string.price_configure) + "：" else ""
                    childBinding.value =
                        "${data.getTitle()}/${data.unitPriceVal}元${if (isPulseDevice) "/${data.pulse}个" else ""}"
                    childBinding.isDefault = data.isDefault
                }
            }
        }
    }

    // 跳转回调界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    // 搜索店铺
                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                            ?.let { selects ->
                                mViewModel.selectDepartments.value = selects
                            }
                    }
                    // 搜索设备型号
                    IntentParams.SearchSelectTypeParam.DeviceModelResultCode -> {
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                GsonUtils.json2List(json, SearchSelectParam::class.java)
                                    ?.let { selected ->
                                        mViewModel.selectDeviceModel.value = selected.firstOrNull()
                                        mViewModel.createDeviceFunConfigure.value = null
                                    }
                            }
                    }
                    // 配置
                    IntentParams.DeviceFunConfigurationV2Params.ResultCode -> {
                        IntentParams.DeviceFunConfigurationV2Params.parseSkuExtAttrDto(intent)
                            ?.let { configures ->
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
            mFunAdapter.refreshList(it, true)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 所属门店 多选
        mBinding.itemDeviceBatchUpdateDepartment.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceBatchUpdateActivity,
                    ShopPositionSelectorActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopPositionSelectorParams.pack(
                            selectList = mViewModel.selectDepartments.value
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
                            shopIdList = mViewModel.selectDepartments.value?.mapNotNull { it.id }
                                ?.toIntArray(),
                            positionIdList = mViewModel.selectDepartments.value?.flatMap {
                                it.positionList?.mapNotNull { item -> item.id } ?: listOf()
                            }?.toIntArray(),
                            mustSelect = true
                        )
                    )
                }
            )
        }

        // 功能配置
        mBinding.itemDeviceBatchUpdateFunConfigure.onSelectedEvent = {
            GsonUtils.json2Class(mViewModel.selectDeviceModel.value?.origin, Spu::class.java)
                ?.let { spu ->
                    startNext.launch(
                        Intent(
                            this,
                            DeviceFunConfigurationV2Activity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.DeviceFunConfigurationV2Params.pack(
                                    mViewModel.selectDeviceModel.value?.id,
                                    mViewModel.selectCategory.value?.code,
                                    spu.communicationType,
                                    GsonUtils.any2Json(spu.extAttrDto),
                                    mViewModel.createDeviceFunConfigure.value,
                                    title = StringUtils.getString(R.string.batch_update)
                                )
                            )
                        }
                    )
                }
        }
        // 功能配置
        mBinding.rvDeviceBatchUpdateSelectFunConfiguration.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.divide_size8
        )?.let {
            mBinding.rvDeviceBatchUpdateSelectFunConfiguration.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvDeviceBatchUpdateSelectFunConfiguration.adapter = mFunAdapter
        mViewModel.jump.observe(this) {
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
                        mViewModel.createDeviceFunConfigure.value = null
                    }
                }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
    }
}