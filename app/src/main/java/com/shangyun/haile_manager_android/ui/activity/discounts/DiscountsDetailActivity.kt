package com.shangyun.haile_manager_android.ui.activity.discounts

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.DiscountsDetailViewModel
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsDetailBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class DiscountsDetailActivity :
    BaseBusinessActivity<ActivityDiscountsDetailBinding, DiscountsDetailViewModel>(
        DiscountsDetailViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val DiscountsId = "discountsId"
        const val Expired = "expired"
    }

    override fun layoutId(): Int = R.layout.activity_discounts_detail
    override fun backBtn(): View = mBinding.barDiscountDetailTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.discountId = intent.getIntExtra(DiscountsId, -1)
        mViewModel.expired = intent.getIntExtra(Expired, -1)
    }

    override fun initEvent() {
        super.initEvent()
        LiveDataBus.with(BusEvents.DISCOUNTS_DETAIL_STATUS)?.observe(this) {
            mViewModel.requestDiscountsDetail()
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.tvDiscountsDetailEdit.setOnClickListener {
            mViewModel.discountsDetail.value?.let {
                startActivity(
                    Intent(
                        this@DiscountsDetailActivity,
                        DiscountsCreateActivity::class.java
                    ).apply {
                        putExtra(DiscountsCreateActivity.OldData, GsonUtils.any2Json(it))
                    }
                )
            }
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel

        mViewModel.requestDiscountsDetail()
    }
}