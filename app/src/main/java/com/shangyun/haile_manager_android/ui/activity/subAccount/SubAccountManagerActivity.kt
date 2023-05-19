package com.shangyun.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SubAccountManagerViewModel
import com.shangyun.haile_manager_android.data.entities.SubAccountEntity
import com.shangyun.haile_manager_android.databinding.ActivitySubAccountManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemSubAccountManagerBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.adapter.GridSameSpaceItemDecoration

class SubAccountManagerActivity :
    BaseBusinessActivity<ActivitySubAccountManagerBinding, SubAccountManagerViewModel>() {

    private val mSubAccountManagerViewModel by lazy {
        getActivityViewModelProvider(this)[SubAccountManagerViewModel::class.java]
    }

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
                    )
                )
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_manager

    override fun getVM(): SubAccountManagerViewModel = mSubAccountManagerViewModel

    override fun backBtn(): View = mBinding.barSubAccountManagerTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasDistributionListPermission.observe(this) {
            if (it) mSubAccountManagerViewModel.requestData()
        }
        mSharedViewModel.hasDistributionAddPermission.observe(this) {
            if (it) initRightBtn()
        }

        mSubAccountManagerViewModel.subAccountList.observe(this) {
            if (!it.isNullOrEmpty()) {
                mAdapter.refreshList(it, true)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvSubAccountList.layoutManager =
            GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false)
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
        mBinding.vm = mSubAccountManagerViewModel
    }
}