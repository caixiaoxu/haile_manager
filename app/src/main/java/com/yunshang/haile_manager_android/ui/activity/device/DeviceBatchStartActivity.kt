package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceBatchStartViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ExtAttrDtoItem
import com.yunshang.haile_manager_android.data.entities.SkuUnionIntersectionEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceBatchStartBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog

class DeviceBatchStartActivity :
    BaseBusinessActivity<ActivityDeviceBatchStartBinding, DeviceBatchStartViewModel>(
        DeviceBatchStartViewModel::class.java, BR.vm
    ) {

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
                                        mViewModel.selectDeviceModel.value = selected
                                        mViewModel.clearSelectFunc()
                                    }
                            }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_batch_start

    override fun backBtn(): View = mBinding.barDeviceBatchStartTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        // 设备类型
        mViewModel.categoryList.observe(this) {
            showDeviceCategoryDialog(it)
        }

        mViewModel.selectDeviceModel.observe(this) {
            mViewModel.requestDeviceSku()
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 所属门店 多选
        mBinding.itemDeviceBatchStartDepartment.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceBatchStartActivity,
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
        mBinding.itemDeviceBatchStartCategory.onSelectedEvent = {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mViewModel.requestDeviceCategory()
        }

        // 设备型号
        mBinding.itemDeviceBatchStartModel.onSelectedEvent = {
            startNext.launch(
                Intent(
                    this@DeviceBatchStartActivity,
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
                            mustSelect = true,
                            moreSelect = true,
                        )
                    )
                }
            )
        }

        // 功能选择
        mBinding.itemDeviceBatchStartFunc.onSelectedEvent = {
            mViewModel.funcList.value?.let { skus ->
                MultiSelectBottomSheetDialog.Builder(
                    StringUtils.getString(
                        if (mViewModel.isDryerOrHair.value == true) R.string.function_model
                        else R.string.function_select
                    ), skus
                ).apply {
                    supportSingle = true
                    onValueSureListener = object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<SkuUnionIntersectionEntity> {
                        override fun onValue(
                            datas: List<SkuUnionIntersectionEntity>,
                            allSelectData: List<SkuUnionIntersectionEntity>
                        ) {
                            datas.firstOrNull()?.let {
                                mViewModel.selectFunc.value = it
                                mViewModel.selectAttr.value = null
                            }
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }
        // 烘干时间选择
        mBinding.itemDeviceBatchStartAttr.onSelectedEvent = {
            mViewModel.selectFunc.value?.extAttrDto?.items?.let { attrs ->
                MultiSelectBottomSheetDialog.Builder(
                    StringUtils.getString(R.string.dryer_time), attrs
                ).apply {
                    supportSingle = true
                    onValueSureListener = object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<ExtAttrDtoItem> {
                        override fun onValue(
                            datas: List<ExtAttrDtoItem>,
                            allSelectData: List<ExtAttrDtoItem>
                        ) {
                            datas.firstOrNull()?.let {
                                mViewModel.selectAttr.value = it
                            }
                        }
                    }
                }.build().show(supportFragmentManager)
            }
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
                        mViewModel.clearSelectFunc()
                    }
                }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
    }
}