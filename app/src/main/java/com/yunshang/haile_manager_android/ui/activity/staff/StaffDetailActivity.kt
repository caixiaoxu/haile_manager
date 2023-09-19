package com.yunshang.haile_manager_android.ui.activity.staff

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.StaffDetailViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams.SearchSelectTypeParam
import com.yunshang.haile_manager_android.databinding.ActivityStaffDetailBinding
import com.yunshang.haile_manager_android.databinding.ItemStaffDetailFlowBinding
import com.yunshang.haile_manager_android.databinding.ItemStaffDetailPermissionBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

class StaffDetailActivity :
    BaseBusinessActivity<ActivityStaffDetailBinding, StaffDetailViewModel>(
        StaffDetailViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val StaffId = "staffId"
    }

    override fun layoutId(): Int = R.layout.activity_staff_detail

    override fun backBtn(): View = mBinding.barStaffDetailManagerTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.staffId = intent.getIntExtra(StaffId, -1)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.staffDetail.observe(this) {
            it?.let { detail ->
                val inflater = LayoutInflater.from(this@StaffDetailActivity)
                // 负责的门店
                buildFlowView(
                    inflater,
                    mBinding.clStaffDetailTakeChargeShop,
                    mBinding.flowStaffDetailShop,
                    detail.shopList
                ) { itemBinding, shop ->
                    itemBinding.name = shop.name
                }

                // 权限
                if (detail.menuList.isNullOrEmpty()) {
                    mBinding.llStaffDetailPermissionList.visibility = View.GONE
                } else {
                    mBinding.llStaffDetailPermissionList.removeViews(
                        1,
                        mBinding.llStaffDetailPermissionList.childCount - 1
                    )
                    val mT = DimensionUtils.dip2px(this@StaffDetailActivity, 24f)
                    detail.menuList.forEachIndexed { index, menu ->
                        inflater.inflate(R.layout.item_staff_detail_permission, null, false)
                            .also { view ->
                                val permissionBinding = ItemStaffDetailPermissionBinding.bind(view)
                                permissionBinding.tvStaffDetailPermissionTitle.text = menu.name
                                buildFlowView(
                                    inflater,
                                    permissionBinding.root,
                                    permissionBinding.flowStaffDetailPermission,
                                    menu.childList
                                ) { itemBinding, menu ->
                                    itemBinding.name = menu.name
                                }
                                mBinding.llStaffDetailPermissionList.addView(
                                    permissionBinding.root, LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(
                                            0,
                                            if (0 == index) DimensionUtils.dip2px(
                                                this@StaffDetailActivity,
                                                16f
                                            ) else mT, 0, 0
                                        )
                                    }
                                )
                            }
                    }
                    mBinding.llStaffDetailPermissionList.visibility = View.VISIBLE
                }
            }
        }

        LiveDataBus.with(BusEvents.STAFF_DETAILS_STATUS)?.observe(this) {
            mViewModel.requestStaffDetailData()
        }

        mViewModel.jump.observe(this) {
            finish()
        }

    }

    /**
     * 构建flow布局
     */
    private fun <T> buildFlowView(
        inflater: LayoutInflater,
        parent: ConstraintLayout,
        flowParent: Flow,
        shopList: List<T>,
        build: (ItemStaffDetailFlowBinding, T) -> Unit
    ) {
        if (shopList.isNullOrEmpty()) {
            parent.visibility = View.GONE
        } else {
            parent.removeViews(2, parent.childCount - 2)
            shopList.forEachIndexed { index, item ->
                DataBindingUtil.inflate<ItemStaffDetailFlowBinding>(
                    inflater,
                    R.layout.item_staff_detail_flow, null, false
                ).also { itemBinding ->
                    build(itemBinding, item)
                    itemBinding.root.id = index + 1
                    parent.addView(itemBinding.root)
                }
            }
            // 设置id
            val idList = IntArray(shopList.size) { it + 1 }
            flowParent.referencedIds = idList
            parent.visibility = View.VISIBLE
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvStaffDetailUpdateShop.setOnClickListener {
            startActivity(
                Intent(
                    this@StaffDetailActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        SearchSelectTypeParam.pack(
                            SearchSelectTypeParam.SearchSelectTypeTakeChargeShop,
                            staffId = mViewModel.staffId,
                            moreSelect = true,
                            selectArr = mViewModel.staffDetail.value?.shopList?.map { shop -> shop.id }
                                ?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }

        mBinding.tvStaffDetailUpdatePermission.setOnClickListener {
            startActivity(Intent(
                this@StaffDetailActivity,
                StaffPermissionActivity::class.java
            ).apply {
                putExtra(
                    StaffPermissionActivity.StaffId,
                    mViewModel.staffId
                )
                putExtra(
                    StaffPermissionActivity.PermissionIds,
                    GsonUtils.any2Json(mViewModel.getPermissionIds())
                )
            })
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel
        mViewModel.requestStaffDetailData()
    }
}