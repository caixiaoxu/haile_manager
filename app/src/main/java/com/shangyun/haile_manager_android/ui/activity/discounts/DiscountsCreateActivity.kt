package com.shangyun.haile_manager_android.ui.activity.discounts

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.DiscountsCreateViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.data.arguments.ActiveDayParam
import com.shangyun.haile_manager_android.data.arguments.DiscountsParam
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.DiscountsBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsDetailEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsDeviceTypeEntity
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.shangyun.haile_manager_android.utils.ActivityManagerUtils
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import com.shangyun.haile_manager_android.utils.ViewUtils
import java.util.*

class DiscountsCreateActivity :
    BaseBusinessActivity<ActivityDiscountsCreateBinding, DiscountsCreateViewModel>() {

    private val mDiscountsCreateViewModel by lazy {
        getActivityViewModelProvider(this)[DiscountsCreateViewModel::class.java]
    }

    companion object{
        const val OldData="oldData"
    }

    // 选择店铺界面
    private val startShopNext =
        ActivityManagerUtils.getActivityResultLauncher(this) { _, rData ->
            rData?.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                    if (selected.isNotEmpty()) {
                        mDiscountsCreateViewModel.createDiscountsShop.value = selected
                        mDiscountsCreateViewModel.deviceCategoryList.value = null
                        mDiscountsCreateViewModel.selectDeviceCategory.value = null
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_discounts_create

    override fun getVM(): DiscountsCreateViewModel = mDiscountsCreateViewModel

    override fun backBtn(): View = mBinding.barDiscountsCreateTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        intent.getStringExtra(OldData)?.let {
            GsonUtils.json2Class(it,DiscountsDetailEntity::class.java)?.let {e->
                mDiscountsCreateViewModel.initOldData(e)
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mDiscountsCreateViewModel.deviceCategoryList.observe(this) {
            if (null != it) {
                showDeviceCategoryDialog(it)
            }
        }
        mDiscountsCreateViewModel.jump.observe(this) {
            finish()
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
            }.build().show(supportFragmentManager, mDiscountsCreateViewModel.startDateTime.value)

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
            }.build().show(supportFragmentManager, mDiscountsCreateViewModel.endDateTime.value)
        }

        // 生效模式
        mBinding.itemDiscountsCreateActiveDayModel.onSelectedEvent = {
            showActiveDayModel()

        }
        mBinding.itemDiscountsCreateActiveDay.onSelectedEvent = {
            showActiveDay()
        }
        // 活动时段
        mBinding.itemDiscountsCreateActiveTimeFrame.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                selectModel = 1
                showModel = 4
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mDiscountsCreateViewModel.activeStartTime.value = date1
                        date2?.let {
                            mDiscountsCreateViewModel.activeEndTime.value = it
                            mDiscountsCreateViewModel.activeTimeFrame.value = String.format(
                                "%s-%s",
                                DateTimeUtils.formatDateTime(date1, "HH:mm"),
                                DateTimeUtils.formatDateTime(it, "HH:mm")
                            )
                        }
                    }
                }
            }.build().show(
                supportFragmentManager,
                mDiscountsCreateViewModel.activeStartTime.value,
                mDiscountsCreateViewModel.activeEndTime.value
            )
        }

        // 输入限制
        ViewUtils.inputAmountLimit(mBinding.itemDiscountsCreateValue.contentView, 1, 1)
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
                            mDiscountsCreateViewModel.deviceCategoryList.value = null
                            mDiscountsCreateViewModel.selectDeviceCategory.value = null
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
        }.build().show(supportFragmentManager)
    }

    /**
     * 显示生效模式
     */
    private fun showActiveDayModel() {
        CommonBottomSheetDialog.Builder(
            StringUtils.getString(R.string.active_day_model),
            DiscountsParam.activeModel
        )
            .apply {
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<ActiveDayParam> {

                        override fun onValue(data: ActiveDayParam) {
                            mDiscountsCreateViewModel.selectActiveModel.value = data
                            mDiscountsCreateViewModel.selectActiveDays.value = null
                        }
                    }
            }.build().show(supportFragmentManager)
    }

    /**
     * 显示活动日
     */
    private fun showActiveDay() {
        val list = GsonUtils.json2List(
            GsonUtils.any2Json(DiscountsParam.activeDay),
            ActiveDayParam::class.java
        )
        list?.let {

            val select = mDiscountsCreateViewModel.selectActiveDays.value
            select?.let {
                list.forEach { type ->
                    type.isCheck = select.contains(type)
                }
            }

            MultiSelectBottomSheetDialog.Builder(
                StringUtils.getString(R.string.active_day),
                list
            ).apply {
                onValueSureListener = object :
                    MultiSelectBottomSheetDialog.OnValueSureListener<ActiveDayParam> {
                    override fun onValue(datas: List<ActiveDayParam>) {
                        mDiscountsCreateViewModel.selectActiveDays.value = datas
                    }
                }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
        mBinding.vm = mDiscountsCreateViewModel

        mDiscountsCreateViewModel.requestData(0)
    }
}