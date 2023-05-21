package com.shangyun.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.business.vm.SubAccountCreateViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.CategoryEntity
import com.shangyun.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import com.shangyun.haile_manager_android.databinding.ActivitySubAccountCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.shangyun.haile_manager_android.utils.ActivityManagerUtils
import java.util.*

class SubAccountCreateActivity :
    BaseBusinessActivity<ActivitySubAccountCreateBinding, SubAccountCreateViewModel>() {

    private val mSubAccountCreateViewModel by lazy {
        getActivityViewModelProvider(this)[SubAccountCreateViewModel::class.java]
    }

    // 选择子账号界面
    private val startSubAccountNext =
        ActivityManagerUtils.getActivityResultLauncher(this) { _, rData ->
            rData?.getStringExtra(SubAccountSelectActivity.SubAccountData)?.let {
                GsonUtils.json2Class(it, StaffEntity::class.java)?.let { e ->
                    mSubAccountCreateViewModel.subAccount.value = e
                }
            }
        }

    // 搜索选择界面
    private val startShopSelectNext =
        ActivityManagerUtils.getActivityResultLauncher(this) { _, rData ->
            rData?.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                    if (selected.isNotEmpty()) {
                        mSubAccountCreateViewModel.subAccountShops.value = selected
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_sub_account_create

    override fun getVM(): SubAccountCreateViewModel = mSubAccountCreateViewModel

    override fun backBtn(): View = mBinding.barSubAccountCreateTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemSubAccountCreateAccount.onSelectedEvent = {
            startSubAccountNext.launch(
                Intent(
                    this@SubAccountCreateActivity,
                    SubAccountSelectActivity::class.java
                ).apply {
                    mSubAccountCreateViewModel.subAccount.value?.let {
                        putExtra(SubAccountSelectActivity.SubAccountData, GsonUtils.any2Json(it))
                    }
                }
            )
        }

        // 业务类型
        mBinding.itemSubAccountCreateBusinessType.onSelectedEvent = {
            mSubAccountCreateViewModel.businessTypeList.value?.let { list ->
                val select = mSubAccountCreateViewModel.businessType.value
                select?.let {
                    list.forEach { type ->
                        type.isCheck = select.contains(type)
                    }
                }

                MultiSelectBottomSheetDialog.Builder("选择业务类型", list).apply {
                    onValueSureListener = object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<ShopBusinessTypeEntity> {
                        override fun onValue(datas: List<ShopBusinessTypeEntity>) {
                            mSubAccountCreateViewModel.businessType.value = datas
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }

        // 设备类型
        mBinding.itemSubAccountCreateDeviceCategory.onSelectedEvent = {
            mSubAccountCreateViewModel.categoryList.value?.let { list ->
                val select = mSubAccountCreateViewModel.deviceCategory.value
                select?.let {
                    list.forEach { type ->
                        type.isCheck = select.contains(type)
                    }
                }

                MultiSelectBottomSheetDialog.Builder("选择设备类型", list).apply {
                    onValueSureListener = object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                        override fun onValue(datas: List<CategoryEntity>) {
                            mSubAccountCreateViewModel.deviceCategory.value = datas
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }

        //分账门店
        mBinding.itemSubAccountCreateShop.onSelectedEvent = {
            startShopSelectNext.launch(
                Intent(
                    this@SubAccountCreateActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtra(
                        SearchSelectRadioActivity.SearchSelectType,
                        SearchSelectRadioViewModel.SearchSelectTypeTakeChargeShop
                    )
                }
            )
        }

        // 生效日
        mBinding.itemSubAccountCreateActiveDate.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                minDate = Calendar.getInstance().apply { time = Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mSubAccountCreateViewModel.effectiveDate.value = date1
                    }
                }
            }.build().show(supportFragmentManager, mSubAccountCreateViewModel.effectiveDate.value)
        }
    }

    override fun initData() {
        mBinding.vm = mSubAccountCreateViewModel
        mSubAccountCreateViewModel.requestData()
    }
}