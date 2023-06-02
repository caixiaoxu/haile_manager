package com.shangyun.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.PersonalInfoViewModel
import com.shangyun.haile_manager_android.data.entities.RoleEntity
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.databinding.ActivityPersonalInfoBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.UpdateValueActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.utils.DialogUtils

class PersonalInfoActivity :
    BaseBusinessActivity<ActivityPersonalInfoBinding, PersonalInfoViewModel>(PersonalInfoViewModel::class.java) {

    override fun layoutId(): Int = R.layout.activity_personal_info

    override fun backBtn(): View = mBinding.barPersonalInfoTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.roleList.observe(this) {
            showRoleListDialog()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.llPersonalInfoHead.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this@PersonalInfoActivity, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mViewModel.uploadHeadIcon(result[0].cutPath) {
                        if (it) {
                            mSharedViewModel.requestUserInfoAsync()
                        } else {
                            SToast.showToast(this@PersonalInfoActivity, R.string.update_failure)
                        }
                    }
                }
            }
        }
        mBinding.tvPersonalInfoName.setOnClickListener {
            startActivity(Intent(this@PersonalInfoActivity, UpdateValueActivity::class.java))
        }

        mBinding.tvPersonalInfoChangeRole.setOnClickListener {
            showRoleListDialog()
        }
    }

    private fun showRoleListDialog() {
        mViewModel.roleList.value?.let {
            if (it.isEmpty()){
                SToast.showToast(this@PersonalInfoActivity,R.string.role_list_empty)
                return@let
            }

            CommonBottomSheetDialog.Builder(
                StringUtils.getString(R.string.select_role_title),
                it
            ).apply {
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<RoleEntity> {
                        override fun onValue(data: RoleEntity) {
                            mViewModel.swapUserLogin(data.id) { login ->
                                SPRepository.loginInfo = login
                                mSharedViewModel.loginInfo.postValue(login)
                                mSharedViewModel.swapUserInfo()
                            }
                        }
                    }
            }.build().show(supportFragmentManager)

        } ?: mViewModel.requestRoleList()
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel
    }
}