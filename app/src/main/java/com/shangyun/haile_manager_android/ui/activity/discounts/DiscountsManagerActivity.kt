package com.shangyun.haile_manager_android.ui.activity.discounts

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.DiscountsManagerViewModel
import com.shangyun.haile_manager_android.data.entities.DiscountsEntity
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemDiscountsManagerBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class DiscountsManagerActivity :
    BaseBusinessActivity<ActivityDiscountsManagerBinding, DiscountsManagerViewModel>() {

    private val mDiscountsManagerViewModel by lazy {
        getActivityViewModelProvider(this)[DiscountsManagerViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_discounts_manager

    override fun getVM(): DiscountsManagerViewModel = mDiscountsManagerViewModel

    override fun backBtn(): View = mBinding.barDiscountsTitle.getBackBtn()

    private val mAdapter: CommonRecyclerAdapter<ItemDiscountsManagerBinding, DiscountsEntity> by lazy {
        CommonRecyclerAdapter(R.layout.item_discounts_manager, BR.item) { mItemBinding, pos, item ->
        }
    }

    override fun initEvent() {
        super.initEvent()
        LiveDataBus.with(BusEvents.DISCOUNTS_LIST_STATUS)?.observe(this) {
            mBinding.rvDiscountsList.requestRefresh()
        }
        // 监听删除
        LiveDataBus.with(BusEvents.DISCOUNTS_LIST_ITEM_DELETE_STATUS, String::class.java)
            ?.observe(this) {
                mAdapter.deleteItem { item -> item.id == it }
            }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        mBinding.rvDiscountsList.layoutManager = LinearLayoutManager(this)
        ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
            mBinding.rvDiscountsList.addItemDecoration(
                DividerItemDecoration(
                    this@DiscountsManagerActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(it)
                })
        }
        mBinding.rvDiscountsList.adapter = mAdapter
        mBinding.rvDiscountsList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DiscountsEntity>() {
                override fun requestData(
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DiscountsEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasDeviceListPermission.value) {
                        mDiscountsManagerViewModel.requestDeviceList(page, pageSize, callBack)
                    }
                }
            }
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDiscountsTitle.getRightBtn(true).run {
            setText(R.string.add_config)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@DiscountsManagerActivity, 4f)
            setOnClickListener {
                startActivity(
                    Intent(
                        this@DiscountsManagerActivity,
                        DiscountsCreateActivity::class.java
                    )
                )
            }
        }
    }

    override fun initData() {
    }
}