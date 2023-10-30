package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.PersonalInfoViewModel
import com.yunshang.haile_manager_android.databinding.ActivityPersonalInfoBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.UpdateValueActivity
import com.yunshang.haile_manager_android.utils.DialogUtils

class PersonalInfoActivity :
    BaseBusinessActivity<ActivityPersonalInfoBinding, PersonalInfoViewModel>(PersonalInfoViewModel::class.java) {

    override fun layoutId(): Int = R.layout.activity_personal_info

    override fun backBtn(): View = mBinding.barPersonalInfoTitle.getBackBtn()

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
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel
    }
}