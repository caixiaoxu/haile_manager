package com.yunshang.haile_manager_android.ui.activity.coupon

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
                                        mViewModel.coupon.value?.organizationType = 2
                                        mViewModel.coupon.value?.shopIds = listOf()
                                    } else {
                                        mViewModel.coupon.value?.organizationType = 3
                                        mViewModel.coupon.value?.shopIds =
                                            selected.map { item -> item.id }
                                    }
                                    mBinding.itemIssueCouponsShop.contentView.setText(
                                        selected.joinToString(
                                            ","
                                        ) { item ->
                                            item.name
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_issue_coupons

    override fun backBtn(): View = mBinding.barIssueCouponsTitle.getBackBtn()

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
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 有效期至
        mBinding.itemIssueCouponsValidity.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                selectModel = 0
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd").let {
                            mViewModel.coupon.value?.validityVal = it
                        }
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
                            selectArr = if (2 == mViewModel.coupon.value?.organizationType)
                                intArrayOf(0)
                            else mViewModel.coupon.value?.shopIds?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }

        // 设备
        mBinding.itemIssueCouponsDevice.onSelectedEvent = {
            MultiSelectBottomSheetDialog.Builder(
                getString(R.string.coupon_device_dialog_title),
                mViewModel.categoryList.value ?: listOf()
            ).apply {
                onValueSureListener =
                    object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                        override fun onValue(
                            selectData: List<CategoryEntity>,
                            allSelectData: List<CategoryEntity>
                        ) {
                            mViewModel.coupon.value?.goodsCategoryIds =
                                selectData.map { item -> item.id }
                            mBinding.itemIssueCouponsDevice.contentView.setText(
                                selectData.joinToString(",") { item -> item.name }
                            )
                        }
                    }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
        mViewModel.requestDeviceCategory()
    }
}