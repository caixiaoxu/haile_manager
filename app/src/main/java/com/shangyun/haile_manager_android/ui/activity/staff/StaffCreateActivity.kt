package com.shangyun.haile_manager_android.ui.activity.staff

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.business.vm.StaffCreateViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.StaffRoleEntity
import com.shangyun.haile_manager_android.databinding.ActivityStaffCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class StaffCreateActivity :
    BaseBusinessActivity<ActivityStaffCreateBinding, StaffCreateViewModel>() {

    private val mStaffCreateViewModel by lazy {
        getActivityViewModelProvider(this)[StaffCreateViewModel::class.java]
    }

    // 店铺选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                SearchSelectRadioActivity.ShopResultCode -> {
                    it.data?.let { intent ->
                        intent.getStringExtra(SearchSelectRadioActivity.ResultData)?.let { json ->
                            GsonUtils.json2Class(json, SearchSelectParam::class.java)
                                ?.let { selected ->
                                    mStaffCreateViewModel.takeChargeShop.value = selected
                                }
                        }
                    }
                }
                StaffPermissionActivity.PermissionResultCode->{
                    it.data?.let { intent->
                        mStaffCreateViewModel.permission.value = intent.getIntegerArrayListExtra(StaffPermissionActivity.PermissionIds)
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_staff_create

    override fun getVM(): StaffCreateViewModel = mStaffCreateViewModel

    override fun backBtn(): View = mBinding.barStaffCreateTitle.getBackBtn()

    private var bottomSheetDialog: CommonBottomSheetDialog<StaffRoleEntity>? = null

    override fun initEvent() {
        super.initEvent()
        mStaffCreateViewModel.roleList.observe(this) {
            if (!it.isNullOrEmpty()) {
                bottomSheetDialog = CommonBottomSheetDialog.Builder(StringUtils.getString(R.string.role_type), it)
                    .apply {
                        onValueSureListener =
                            object : CommonBottomSheetDialog.OnValueSureListener<StaffRoleEntity> {
                                override fun onValue(data: StaffRoleEntity) {
                                    mStaffCreateViewModel.role.value = data
                                }
                            }
                    }.build()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemStaffCreateRole.onSelectedEvent = {
            bottomSheetDialog?.show(supportFragmentManager)
        }
        mBinding.itemStaffCreateTakeChargeShop.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@StaffCreateActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtra(
                        SearchSelectRadioActivity.SearchSelectType,
                        SearchSelectRadioViewModel.SearchSelectTypeTakeChargeShop
                    )
                }
            )
        }
        mBinding.itemStaffCreatePermission.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@StaffCreateActivity,
                    StaffPermissionActivity::class.java
                )
            )
        }
    }

    override fun initData() {
        mBinding.vm = mStaffCreateViewModel

        mStaffCreateViewModel.requestRoleList()
    }
}