package com.shangyun.haile_manager_android.ui.activity.staff

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.business.vm.StaffPermissionViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.arguments.StaffPermissionParams
import com.shangyun.haile_manager_android.data.entities.ChangeUserEntity
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.databinding.ActivityStaffPermissionBinding
import com.shangyun.haile_manager_android.databinding.ItemChangeAccountBinding
import com.shangyun.haile_manager_android.databinding.ItemStaffPermissionBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.shangyun.haile_manager_android.utils.UserPermissionUtils

class StaffPermissionActivity :
    BaseBusinessActivity<ActivityStaffPermissionBinding, StaffPermissionViewModel>() {

    private val mStaffPermissionViewModel by lazy {
        getActivityViewModelProvider(this)[StaffPermissionViewModel::class.java]
    }

    companion object {
        const val PermissionResultCode = 0x90100
        const val PermissionIds = "permissionIds"
    }

    override fun layoutId(): Int = R.layout.activity_staff_permission

    override fun getVM(): StaffPermissionViewModel = mStaffPermissionViewModel

    override fun backBtn(): View = mBinding.barStaffPermissionTitle.getBackBtn()

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemStaffPermissionBinding, StaffPermissionParams>(
            R.layout.item_staff_permission, BR.item,
        ) { mBinding, _, data ->
            mBinding?.root?.setOnClickListener {
                MultiSelectBottomSheetDialog.Builder(data.parent.name, data.child ?: arrayListOf())
                    .apply {
                        onValueSureListener = object :
                            MultiSelectBottomSheetDialog.OnValueSureListener<UserPermissionEntity> {
                            override fun onValue(datas: List<UserPermissionEntity>) {
                                mStaffPermissionViewModel.checkSelectAll();
                                data.notifyPropertyChanged(BR.selectNum)
                            }
                        }
                    }.build()
                    .show(supportFragmentManager)
            }
        }
    }

    override fun initIntent() {
        super.initIntent()

        mStaffPermissionViewModel.selectList =
            intent.getIntegerArrayListExtra(PermissionIds) ?: arrayListOf()
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasUserPermission.observe(this) {
            mStaffPermissionViewModel.dealPermissionList()
        }

        mStaffPermissionViewModel.permissionList.observe(this) {
            if (!it.isNullOrEmpty()) {
                mAdapter.refreshList(it, true)
                mStaffPermissionViewModel.checkSelectAll()
            }
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
                setResult(PermissionResultCode, Intent().apply {
                    putExtra(PermissionIds, mStaffPermissionViewModel.getSelectPermissionIds())
                })
                finish()
            }
        }

        ResourcesCompat.getDrawable(
            resources,
            R.drawable.divder_efefef_size_half,
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
        mBinding.vm = mStaffPermissionViewModel
    }
}