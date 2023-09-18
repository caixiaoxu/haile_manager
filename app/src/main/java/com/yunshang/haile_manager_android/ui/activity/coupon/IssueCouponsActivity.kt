package com.yunshang.haile_manager_android.ui.activity.coupon

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IssueCouponsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.databinding.ActivityIssueCouponsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.ViewUtils
import java.util.*

class IssueCouponsActivity :
    BaseBusinessActivity<ActivityIssueCouponsBinding, IssueCouponsViewModel>(
        IssueCouponsViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                if (selected.isNotEmpty()) {
                                    if (selected.any { item -> 0 == item.id }) {
                                        mViewModel.coupon.value?.organizationType = 1
                                        mViewModel.coupon.value?.shopIds = null
                                    } else {
                                        mViewModel.coupon.value?.organizationType = 3
                                        mViewModel.coupon.value?.shopIds =
                                            selected.map { item -> item.id }
                                    }
                                    mViewModel.coupon.value?.shopNameVal =
                                        selected.joinToString("、") { item -> item.name }
                                    refreshShopName()
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_issue_coupons

    override fun backBtn(): View = mBinding.barIssueCouponsTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 券金额
        ViewUtils.inputAmountLimit(mBinding.itemIssueCouponsPrice.contentView, 3, 2)
        // 体验价
        ViewUtils.inputAmountLimit(mBinding.itemIssueCouponsExperientialPrice.contentView, 3, 2)
        // 券折扣
        ViewUtils.inputAmountLimit(mBinding.itemIssueCouponsPercentage.contentView, 1, 1)
        // 减免上限
        ViewUtils.inputAmountLimit(mBinding.itemIssueCouponsMaxPrice.contentView, 3, 2)
        // 使用条件
        ViewUtils.inputAmountLimit(mBinding.etIssueCouponsReachPrice, 3, 2)

        // 券类型
        mBinding.itemIssueCouponsType.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                getString(
                    R.string.coupon_type
                ), mViewModel.couponTypeList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        data?.id?.let {
                            mViewModel.coupon.value?.couponTypeVal = it
                            // 体验券，没有全部设备，单选
                            if (4 == mViewModel.coupon.value?.couponTypeVal
                                && (null != mViewModel.coupon.value!!.goodsCategoryIds
                                        && (mViewModel.coupon.value!!.goodsCategoryIds!!.contains(0)
                                        || mViewModel.coupon.value!!.goodsCategoryIds!!.size > 1))
                            ) {
                                mViewModel.categoryList.value?.forEach { category ->
                                    category.isCheck = false
                                }
                                mViewModel.coupon.value?.goodsCategoryIds = null
                                refreshCategoryName()
                            }
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 有效期至
        mBinding.itemIssueCouponsValidity.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                selectModel = 0
                minDate = Calendar.getInstance().apply { time = Date() }
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        mViewModel.coupon.value?.validityVal =
                            DateTimeUtils.formatDateTimeEndParam(date1)
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 店铺
        mBinding.itemIssueCouponsShop.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@IssueCouponsActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeCouponShop,
                            mustSelect = true,
                            moreSelect = true,
                            hasAll = true,
                            selectArr = if (1 == mViewModel.coupon.value?.organizationType)
                                intArrayOf(0)
                            else mViewModel.coupon.value?.shopIds?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }
        refreshShopName()

        // 设备
        mBinding.itemIssueCouponsDevice.onSelectedEvent = {
            val list = mutableListOf<CategoryEntity>()
            // 体验券没有全部设备
            if (4 != mViewModel.coupon.value?.couponType) {
                list.add(
                    0,
                    CategoryEntity(
                        id = 0,
                        name = StringUtils.getString(R.string.all_device)
                    ).apply {
                        onlyOne = true
                        isCheck = true == mViewModel.coupon.value?.goodsCategoryIds?.contains(0)
                    })
            }
            mViewModel.categoryList.value?.let {
                list.addAll(it)
            }

            MultiSelectBottomSheetDialog.Builder(
                getString(R.string.coupon_device_dialog_title), list
            ).apply {
                supportSingle = 4 == mViewModel.coupon.value?.couponType
                onValueSureListener =
                    object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                        override fun onValue(
                            selectData: List<CategoryEntity>,
                            allSelectData: List<CategoryEntity>
                        ) {
                            mViewModel.coupon.value?.goodsCategoryIds =
                                selectData.map { item -> item.id }
                            mViewModel.coupon.value?.categoryNameVal =
                                selectData.joinToString("、") { item -> item.name }
                            refreshCategoryName()
                        }
                    }
            }.build().show(supportFragmentManager)
        }
        refreshCategoryName()
    }

    private fun refreshShopName() {
        mBinding.itemIssueCouponsShop.contentView.setText(
            mViewModel.coupon.value?.shopNameVal ?: ""
        )
    }

    private fun refreshCategoryName() {
        mBinding.itemIssueCouponsDevice.contentView.setText(
            mViewModel.coupon.value?.categoryNameVal ?: ""
        )
    }

    override fun initData() {
        mViewModel.requestDeviceCategory()
    }
}