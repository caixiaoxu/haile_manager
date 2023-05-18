package com.shangyun.haile_manager_android.ui.activity.discounts

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DiscountsCreateViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.DiscountsBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsDeviceTypeEntity
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.shangyun.haile_manager_android.utils.ActivityManagerUtils
import java.util.*

class DiscountsCreateActivity :
    BaseBusinessActivity<ActivityDiscountsCreateBinding, DiscountsCreateViewModel>() {

    private val mDiscountsCreateViewModel by lazy {
        getActivityViewModelProvider(this)[DiscountsCreateViewModel::class.java]
    }

    // 选择店铺界面
    private val startShopNext =
        ActivityManagerUtils.getActivityResultLauncher(this) { _, rData ->
            rData?.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                    if (selected.isNotEmpty()) {
                        mDiscountsCreateViewModel.createDiscountsShop.value = selected
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_discounts_create

    override fun getVM(): DiscountsCreateViewModel = mDiscountsCreateViewModel

    override fun backBtn(): View = mBinding.barDiscountsCreateTitle.getBackBtn()


    override fun initEvent() {
        super.initEvent()

        mDiscountsCreateViewModel.deviceCategoryList.observe(this) {
            if (null != it) {
                showDeviceCategoryDialog(it)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 所属门店
        mBinding.itemDiscountsCreateShop.onSelectedEvent = {
            startShopNext.launch(
                Intent(
                    this@DiscountsCreateActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtra(
                        SearchSelectRadioActivity.SearchSelectType,
                        SearchSelectRadioViewModel.SearchSelectTypeTakeChargeShop
                    )
                })
        }

        // 业务类型
        mBinding.itemDiscountsCreateBusinessType.onSelectedEvent = {
            showBusinessTypeDialog()
        }

        // 设备类型
        mBinding.itemDiscountsCreateDeviceCategory.onSelectedEvent = {
            mDiscountsCreateViewModel.deviceCategoryList.value?.let {
                showDeviceCategoryDialog(it)
            } ?: mDiscountsCreateViewModel.requestData(1)
        }

        // 开始时间
        mBinding.itemDiscountsCreateStartTime.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                minDate = Calendar.getInstance().apply { time = Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mDiscountsCreateViewModel.startDateTime.value = date1
                    }
                }
            }.build().show(supportFragmentManager)

        }

        // 结束时间
        mBinding.itemDiscountsCreateEndTime.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                minDate =
                    Calendar.getInstance()
                        .apply { time = mDiscountsCreateViewModel.startDateTime.value ?: Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mDiscountsCreateViewModel.endDateTime.value = date1
                    }
                }
            }.build().show(supportFragmentManager)
        }
    }

    /**
     * 显示业务弹窗
     */
    private fun showBusinessTypeDialog() {
        mDiscountsCreateViewModel.shopBusinessTypeList.value?.let { list ->
            CommonBottomSheetDialog.Builder(StringUtils.getString(R.string.business_type), list)
                .apply {
                    onValueSureListener = object :
                        CommonBottomSheetDialog.OnValueSureListener<DiscountsBusinessTypeEntity> {

                        override fun onValue(data: DiscountsBusinessTypeEntity) {
                            mDiscountsCreateViewModel.selectBusinessType.value = data
                        }
                    }
                }.build().show(supportFragmentManager)
        }
    }

    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceCategoryDialog(deviceTypes: List<DiscountsDeviceTypeEntity>) {
        val select = mDiscountsCreateViewModel.selectDeviceCategory.value
        select?.let {
            deviceTypes.forEach { type ->
                type.isCheck = select.contains(type)
            }
        }

        val multiDialog =
            MultiSelectBottomSheetDialog.Builder(
                StringUtils.getString(R.string.device_category),
                deviceTypes
            ).apply {
                onValueSureListener = object :
                    MultiSelectBottomSheetDialog.OnValueSureListener<DiscountsDeviceTypeEntity> {
                    override fun onValue(datas: List<DiscountsDeviceTypeEntity>) {
                        mDiscountsCreateViewModel.selectDeviceCategory.value = datas
                    }
                }
            }.build()
        multiDialog.show(supportFragmentManager)
    }

    override fun initData() {
        mBinding.vm = mDiscountsCreateViewModel

        mDiscountsCreateViewModel.requestData(0)
    }
}