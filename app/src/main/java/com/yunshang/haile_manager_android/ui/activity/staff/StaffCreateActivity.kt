package com.yunshang.haile_manager_android.ui.activity.staff

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.StaffCreateViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams.SearchSelectTypeParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.StaffRoleEntity
import com.yunshang.haile_manager_android.databinding.ActivityStaffCreateBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

class StaffCreateActivity :
    BaseBusinessActivity<ActivityStaffCreateBinding, StaffCreateViewModel>(
        StaffCreateViewModel::class.java,
        BR.vm
    ) {

    // 店铺选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                SearchSelectTypeParam.ShopResultCode -> {
                    it.data?.let { intent ->
                        intent.getStringExtra(SearchSelectTypeParam.ResultData)?.let { json ->
                            GsonUtils.json2List(json, SearchSelectParam::class.java)
                                ?.let { selected ->
                                    mViewModel.takeChargeShop.value = selected
                                }
                        }
                    }
                }
                StaffPermissionActivity.PermissionResultCode -> {
                    it.data?.let { intent ->
                        mViewModel.permission.value =
                            intent.getIntegerArrayListExtra(StaffPermissionActivity.PermissionIds)
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_staff_create

    override fun backBtn(): View = mBinding.barStaffCreateTitle.getBackBtn()

    private var bottomSheetDialog: CommonBottomSheetDialog<StaffRoleEntity>? = null

    override fun initEvent() {
        super.initEvent()
        mViewModel.roleList.observe(this) {
            if (!it.isNullOrEmpty()) {
                bottomSheetDialog =
                    CommonBottomSheetDialog.Builder(StringUtils.getString(R.string.role_type), it)
                        .apply {
                            onValueSureListener =
                                object :
                                    CommonBottomSheetDialog.OnValueSureListener<StaffRoleEntity> {
                                    override fun onValue(data: StaffRoleEntity) {
                                        mViewModel.role.value = data
                                    }
                                }
                        }.build()
            }
        }
        mViewModel.jump.observe(this) {
            finish()
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
                    putExtras(putExtras(SearchSelectTypeParam.pack(SearchSelectTypeParam.SearchSelectTypeTakeChargeShop)))
                }
            )
        }
        mBinding.itemStaffCreatePermission.onSelectedEvent = {
            startSearchSelect.launch(
                Intent(
                    this@StaffCreateActivity,
                    StaffPermissionActivity::class.java
                ).apply {
                    putExtra(
                        StaffPermissionActivity.PermissionIds,
                        mViewModel.permission.value ?: intArrayOf()
                    )
                }
            )
        }
    }

    override fun initData() {
        mViewModel.requestRoleList()
    }
}