package com.yunshang.haile_manager_android.ui.activity.coupon

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.IssueCouponsViewModel
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.databinding.ActivityIssueCouponsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.ViewUtils
import java.util.*

class IssueCouponsActivity :
    BaseBusinessActivity<ActivityIssueCouponsBinding, IssueCouponsViewModel>(
        IssueCouponsViewModel::class.java, BR.vm
    ) {

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
    }

    override fun initData() {
    }
}