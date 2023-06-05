package com.yunshang.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.yunshang.haile_manager_android.business.vm.SubAccountCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.yunshang.haile_manager_android.data.entities.StaffEntity
import com.yunshang.haile_manager_android.databinding.ActivitySubAccountCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.ActivityManagerUtils
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

class SubAccountCreateActivity :
    BaseBusinessActivity<ActivitySubAccountCreateBinding, SubAccountCreateViewModel>(SubAccountCreateViewModel::class.java,BR.vm) {

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
                    mViewModel.subAccount.value = e
                }
            }
        }

    // 搜索选择界面
    private val startShopSelectNext =
        ActivityManagerUtils.getActivityResultLauncher(this) { _, rData ->
            rData?.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                    if (selected.isNotEmpty()) {
                        mViewModel.subAccountShops.value = selected
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_sub_account_create

    override fun backBtn(): View = mBinding.barSubAccountCreateTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        intent.getIntExtra(UserId, -1).let {
            mViewModel.userId = it
            if (-1 != it) {
                mViewModel.categoryIds =
                    intent.getIntArrayExtra(CategoryIds) ?: intArrayOf()
                mViewModel.shopIds =
                    intent.getIntArrayExtra(ShopIds) ?: intArrayOf()
                mViewModel.subAccountRatio.value = intent.getStringExtra(Ratio)
                mViewModel.effectiveDate.value =
                    DateTimeUtils.formatDateFromString(intent.getStringExtra(EffectiveDate))
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.jump.observe(this) {
            finish()
        }

        mViewModel.categoryList.observe(this) {
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
                    mViewModel.subAccount.value?.let {
                        putExtra(SubAccountSelectActivity.SubAccountData, GsonUtils.any2Json(it))
                    }
                }
            )
        }

        // 业务类型
        mBinding.itemSubAccountCreateBusinessType.onSelectedEvent = {
            mViewModel.businessTypeList.value?.let { list ->
                CommonBottomSheetDialog.Builder(
                    StringUtils.getString(R.string.business_type_title),
                    list
                ).apply {
                    onValueSureListener = object :
                        CommonBottomSheetDialog.OnValueSureListener<ShopBusinessTypeEntity> {
                        override fun onValue(data: ShopBusinessTypeEntity) {
                            mViewModel.businessType.value = data
                            mViewModel.categoryList.value = null
                            mViewModel.deviceCategory.value = null
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }

        // 设备类型
        mBinding.itemSubAccountCreateDeviceCategory.onSelectedEvent = {
            mViewModel.categoryList.value?.let { list ->
                showDeviceCategoryDailog(list)
            } ?: mViewModel.requestDeviceCategoryList()

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
                        mViewModel.effectiveDate.value = date1
                    }
                }
            }.build().show(supportFragmentManager, mViewModel.effectiveDate.value)
        }
    }

    private fun showDeviceCategoryDailog(list: List<CategoryEntity>) {
        val select = mViewModel.deviceCategory.value
        select?.let {
            list.forEach { type ->
                type.isCheck = select.contains(type)
            }
        }

        MultiSelectBottomSheetDialog.Builder("选择设备类型", list).apply {
            onValueSureListener = object :
                MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                override fun onValue(datas: List<CategoryEntity>) {
                    mViewModel.deviceCategory.value = datas
                }
            }
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}