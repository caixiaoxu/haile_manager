package com.yunshang.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.SubAccountManagerViewModel
import com.yunshang.haile_manager_android.data.entities.SubAccountEntity
import com.yunshang.haile_manager_android.databinding.ActivitySubAccountManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemSubAccountManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.GridSameSpaceItemDecoration

class SubAccountManagerActivity :
    BaseBusinessActivity<ActivitySubAccountManagerBinding, SubAccountManagerViewModel>(SubAccountManagerViewModel::class.java,BR.vm) {

    private val mAdapter: CommonRecyclerAdapter<ItemSubAccountManagerBinding, SubAccountEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_sub_account_manager,
            BR.item
        ) { mItemBinding, _, item ->

            mItemBinding?.root?.setOnClickListener {
                startActivity(
                    Intent(
                        this@SubAccountManagerActivity,
                        SubAccountDetailActivity::class.java
                    ).apply {
                        putExtra(SubAccountDetailActivity.UserId, item.id)
                    }
                )
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_manager

    override fun backBtn(): View = mBinding.barSubAccountManagerTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasDistributionListPermission.observe(this) {
            if (it) mViewModel.requestData()
        }
        mSharedViewModel.hasDistributionAddPermission.observe(this) {
            if (it) initRightBtn()
        }

        mViewModel.subAccountList.observe(this) {
            if (!it.isNullOrEmpty()) {
                mAdapter.refreshList(it, true)
            }
        }

        LiveDataBus.with(BusEvents.SUB_ACCOUNT_LIST_STATUS)?.observe(this) {
            mViewModel.requestData()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvSubAccountList.layoutManager =
            GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        val space = DimensionUtils.dip2px(this, 12f)
        mBinding.rvSubAccountList.addItemDecoration(GridSameSpaceItemDecoration(space, space))
        mBinding.rvSubAccountList.adapter = mAdapter
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {

        mBinding.barSubAccountManagerTitle.getRightBtn(true).run {
            setText(R.string.add_config)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@SubAccountManagerActivity, 4f)
            setOnClickListener {
                startActivity(
                    Intent(
                        this@SubAccountManagerActivity,
                        SubAccountCreateActivity::class.java
                    )
                )
            }
        }
    }

    override fun initData() {
    }
}