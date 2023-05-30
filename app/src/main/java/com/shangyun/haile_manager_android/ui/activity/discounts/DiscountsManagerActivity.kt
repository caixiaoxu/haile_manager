package com.shangyun.haile_manager_android.ui.activity.discounts

import android.content.Intent
import android.graphics.Color
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.DiscountsManagerViewModel
import com.shangyun.haile_manager_android.data.entities.DiscountsEntity
import com.shangyun.haile_manager_android.databinding.ActivityDiscountsManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemDiscountsManagerBinding
import com.shangyun.haile_manager_android.databinding.ItemDiscountsTagFlowBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView

class DiscountsManagerActivity :
    BaseBusinessActivity<ActivityDiscountsManagerBinding, DiscountsManagerViewModel>(
        DiscountsManagerViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_discounts_manager

    override fun backBtn(): View = mBinding.barDiscountsTitle.getBackBtn()

    private val mAdapter: CommonRecyclerAdapter<ItemDiscountsManagerBinding, DiscountsEntity> by lazy {
        CommonRecyclerAdapter(R.layout.item_discounts_manager, BR.item) { mItemBinding, _, item ->
            mItemBinding?.clDiscountsParent?.let { parent ->
                if (parent.childCount > 6) {
                    parent.removeViews(6, parent.childCount - 6)
                }

                val inflater = LayoutInflater.from(this@DiscountsManagerActivity)
                // 业务类型
                DataBindingUtil.inflate<ItemDiscountsTagFlowBinding>(
                    inflater, R.layout.item_discounts_tag_flow, null, false
                ).also { itemBinding ->
                    itemBinding.name = item.bizTypeName
                    itemBinding.root.id = 1
                    parent.addView(itemBinding.root)
                }
                // 设备类型
                item.categoryList.forEachIndexed { index, category ->
                    DataBindingUtil.inflate<ItemDiscountsTagFlowBinding>(
                        inflater, R.layout.item_discounts_tag_flow, null, false
                    ).also { itemBinding ->
                        itemBinding.name = category.name
                        itemBinding.root.id = index + 2
                        parent.addView(itemBinding.root)
                    }
                }
                // 设置id
                val idList = IntArray(item.categoryList.size + 1) { it + 1 }
                mItemBinding.flowDiscountsTags.referencedIds = idList
            }

            val valStr = StringUtils.getString(R.string.num_folds, item.discountVO)
            mItemBinding?.tvDiscountsValue?.text =
                com.shangyun.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                    valStr, arrayOf(
                        ForegroundColorSpan(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        ),
                        AbsoluteSizeSpan(DimensionUtils.sp2px(this@DiscountsManagerActivity, 32f))
                    ), 0, valStr.length - 1
                )

            mItemBinding?.root?.setOnClickListener {
                if (mSharedViewModel.hasMarketingInfoPermission.value == true) {
                    startActivity(
                        Intent(
                            this@DiscountsManagerActivity,
                            DiscountsDetailActivity::class.java
                        ).apply {
                            putExtra(DiscountsDetailActivity.DiscountsId, item.id)
                            putExtra(DiscountsDetailActivity.Expired, item.expired)
                        }
                    )
                }
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasMarketingListPermission.observe(this) {
            if (it) mBinding.rvDiscountsList.requestRefresh()
        }
        mSharedViewModel.hasMarketingInfoPermission.observe(this) {}
        mSharedViewModel.hasMarketingAddPermission.observe(this) {
            if (it) initRightBtn()
        }

        LiveDataBus.with(BusEvents.DISCOUNTS_LIST_STATUS)?.observe(this) {
            mBinding.rvDiscountsList.requestRefresh()
        }
        // 监听删除
        LiveDataBus.with(BusEvents.DISCOUNTS_LIST_ITEM_DELETE_STATUS, Int::class.java)
            ?.observe(this) {
                mAdapter.deleteItem { item -> item.id == it }
            }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

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
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DiscountsEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasMarketingListPermission.value) {
                        mViewModel.requestDeviceList(page, pageSize, callBack)
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