package com.shangyun.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
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
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import java.util.*

class SubAccountCreateActivity :
    BaseBusinessActivity<ActivitySubAccountCreateBinding, SubAccountCreateViewModel>() {

    private val mSubAccountCreateViewModel by lazy {
        getActivityViewModelProvider(this)[SubAccountCreateViewModel::class.java]
    }

    companion object {
        const val UserId = "userId"
        const val CategoryIds = "categoryIds"
        const val ShopIds = "shopIds"
        const val Ratio = "ratio"
        const val EffectiveDate = "effectiveDate"
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

    override fun initIntent() {
        super.initIntent()

        intent.getIntExtra(UserId, -1).let {
            mSubAccountCreateViewModel.userId = it
            if (-1 != it) {
                mSubAccountCreateViewModel.categoryIds =
                    intent.getIntArrayExtra(CategoryIds) ?: intArrayOf()
                mSubAccountCreateViewModel.shopIds =
                    intent.getIntArrayExtra(ShopIds) ?: intArrayOf()
                mSubAccountCreateViewModel.subAccountRatio.value = intent.getStringExtra(Ratio)
                mSubAccountCreateViewModel.effectiveDate.value =
                    DateTimeUtils.formatDateFromString(intent.getStringExtra(EffectiveDate))
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSubAccountCreateViewModel.jump.observe(this) {
            finish()
        }

        mSubAccountCreateViewModel.categoryList.observe(this) {
            if (it.isNullOrEmpty()) showDeviceCategoryDailog(it)
        }
    }

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
                CommonBottomSheetDialog.Builder(
                    StringUtils.getString(R.string.business_type_title),
                    list
                ).apply {
                    onValueSureListener = object :
                        CommonBottomSheetDialog.OnValueSureListener<ShopBusinessTypeEntity> {
                        override fun onValue(data: ShopBusinessTypeEntity) {
                            mSubAccountCreateViewModel.businessType.value = data
                            mSubAccountCreateViewModel.categoryList.value = null
                            mSubAccountCreateViewModel.deviceCategory.value = null
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }

        // 设备类型
        mBinding.itemSubAccountCreateDeviceCategory.onSelectedEvent = {
            mSubAccountCreateViewModel.categoryList.value?.let { list ->
                showDeviceCategoryDailog(list)
            } ?: mSubAccountCreateViewModel.requestDeviceCategoryList()

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

    private fun showDeviceCategoryDailog(list: List<CategoryEntity>) {
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

    override fun initData() {
        mBinding.vm = mSubAccountCreateViewModel
        mSubAccountCreateViewModel.requestData()
    }
}