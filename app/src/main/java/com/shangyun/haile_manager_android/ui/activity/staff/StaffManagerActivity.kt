package com.shangyun.haile_manager_android.ui.activity.staff

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.business.vm.StaffManagerViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.OrderListEntity
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import com.shangyun.haile_manager_android.databinding.ActivityStaffManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemStaffManagerBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.activity.shop.ShopCreateAndUpdateActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class StaffManagerActivity :
    BaseBusinessActivity<ActivityStaffManagerBinding, StaffManagerViewModel>() {
    private val mStaffManagerViewModel by lazy {
        getActivityViewModelProvider(this)[StaffManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_staff_manager

    override fun getVM(): StaffManagerViewModel = mStaffManagerViewModel

    override fun backBtn(): View = mBinding.barStaffManagerTitle.getBackBtn()

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemStaffManagerBinding, StaffEntity>(
            R.layout.item_staff_manager, BR.item
        ) { mItemBinding, pos, item ->

        }
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasPersonAddPermission.observe(this) {
            if (it) initRightBtn()
        }
        mSharedViewModel.hasPersonListPermission.observe(this) {}
        mSharedViewModel.hasPersonInfoPermission.observe(this) {}
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barStaffManagerTitle.getRightBtn(true).run {
            setText(R.string.add_shop)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@StaffManagerActivity, 4f)
            setOnClickListener {
                startActivity(
                    Intent(
                        this@StaffManagerActivity,
                        StaffCreateActivity::class.java
                    )
                )
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvStaffManagerList.layoutManager = LinearLayoutManager(this)
        ContextCompat.getDrawable(
            this,
            R.drawable.shape_bottom_stroke_dividing_efefef_mlr16
        )?.let {
            mBinding.rvStaffManagerList.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                ).apply { setDrawable(it) })
        }
        mBinding.rvStaffManagerList.adapter = mAdapter
        mBinding.rvStaffManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<StaffEntity>() {
                override fun requestData(
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out StaffEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasPersonListPermission.value) {
                        mStaffManagerViewModel.requestStaffList(page, pageSize, callBack)
                    }
                }
            }
    }

    override fun initData() {
        mBinding.rvStaffManagerList.requestRefresh()
    }
}