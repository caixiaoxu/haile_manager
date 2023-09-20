package com.yunshang.haile_manager_android.ui.activity.staff

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.StaffPermissionViewModel
import com.yunshang.haile_manager_android.data.arguments.StaffPermission
import com.yunshang.haile_manager_android.data.arguments.StaffPermissionParams
import com.yunshang.haile_manager_android.data.entities.DataPermissionShopDto
import com.yunshang.haile_manager_android.data.entities.UserPermissionEntity
import com.yunshang.haile_manager_android.databinding.ActivityStaffPermissionBinding
import com.yunshang.haile_manager_android.databinding.ItemStaffPermissionBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.ProfitDataDialog

class StaffPermissionActivity :
    BaseBusinessActivity<ActivityStaffPermissionBinding, StaffPermissionViewModel>(
        StaffPermissionViewModel::class.java,
        BR.vm
    ) {

    companion object {
        const val PermissionResultCode = 0x90100
        const val StaffId = "staffId"
        const val PermissionIds = "permissionIds"
        const val ShopList = "ShopList"
        const val ProfitTypes = "ProfitTypes"
    }

    override fun layoutId(): Int = R.layout.activity_staff_permission

    override fun backBtn(): View = mBinding.barStaffPermissionTitle.getBackBtn()

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemStaffPermissionBinding, StaffPermissionParams>(
            R.layout.item_staff_permission, BR.item,
        ) { mBinding, _, data ->
            mBinding?.root?.setOnClickListener {
                if (data.parent.perms == "league:profit") {
                    ProfitDataDialog.Builder(
                        GsonUtils.json2Class(
                            GsonUtils.any2Json(data),
                            StaffPermissionParams::class.java
                        ),
                        GsonUtils.json2List(
                            GsonUtils.any2Json(mViewModel.shopList),
                            DataPermissionShopDto::class.java
                        ),
                        mViewModel.fundsDistributionTypes
                    ) { childPermission, shopList, types ->
                        data.child = childPermission
                        mViewModel.shopList = shopList
                        mViewModel.fundsDistributionTypes = types
                        mViewModel.checkSelectAll()
                        data.notifyPropertyChanged(BR.selectNum)
                    }.build().show(supportFragmentManager)
                } else {
                    MultiSelectBottomSheetDialog.Builder(
                        data.parent.name,
                        data.child ?: arrayListOf()
                    )
                        .apply {
                            isCanSelectEmpty = true
                            onValueSureListener = object :
                                MultiSelectBottomSheetDialog.OnValueSureListener<UserPermissionEntity> {

                                override fun onValue(
                                    selectData: List<UserPermissionEntity>,
                                    allSelectData: List<UserPermissionEntity>
                                ) {
                                    allSelectData.forEach { permission ->
                                        permission.isOldCheck = permission.isCheck
                                    }
                                    mViewModel.checkSelectAll()
                                    data.notifyPropertyChanged(BR.selectNum)
                                }
                            }
                            onCancelListener = {
                                list.forEach { permission ->
                                    permission.isCheck = permission.isOldCheck
                                }
                            }
                        }.build()
                        .show(supportFragmentManager)
                }
            }
        }
    }

    override fun initIntent() {
        super.initIntent()

        mViewModel.staffId = intent.getIntExtra(StaffId, -1)
        mViewModel.selectList =
            GsonUtils.json2List(intent.getStringExtra(PermissionIds), StaffPermission::class.java)
                ?: mutableListOf()

        mViewModel.shopList =
            GsonUtils.json2List(intent.getStringExtra(ShopList), DataPermissionShopDto::class.java)

        mViewModel.fundsDistributionTypes = intent.getIntArrayExtra(ProfitTypes)?.toList()
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasUserPermission.observe(this) {
            mViewModel.dealPermissionList()
        }

        mViewModel.permissionList.observe(this) {
            if (!it.isNullOrEmpty()) {
                mAdapter.refreshList(it, true)
                mViewModel.checkSelectAll()
            }
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barStaffPermissionTitle.getRightBtn().run {
            setText(R.string.finish)
            setTextColor(
                ContextCompat.getColor(
                    this@StaffPermissionActivity,
                    R.color.colorPrimary
                )
            )
            setOnClickListener {
                if (-1 == mViewModel.staffId) {
                    setResult(PermissionResultCode, Intent().apply {
                        putExtra(
                            PermissionIds,
                            GsonUtils.any2Json(mViewModel.getSelectPermissionIds())
                        )
                        putExtra(
                            ShopList,
                            GsonUtils.any2Json(mViewModel.shopList)
                        )
                        putExtra(
                            ProfitTypes, mViewModel.fundsDistributionTypes?.toIntArray()
                        )
                    })
                    finish()
                } else {
                    mViewModel.updateStaffPermission(this@StaffPermissionActivity)
                }
            }
        }

        ResourcesCompat.getDrawable(
            resources,
            R.drawable.divder_efefef,
            null
        )?.let { drawable ->
            mBinding.rvStaffPermissionList.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(drawable)
                })
        }
        mBinding.rvStaffPermissionList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvStaffPermissionList.adapter = mAdapter
    }

    override fun initData() {
    }
}