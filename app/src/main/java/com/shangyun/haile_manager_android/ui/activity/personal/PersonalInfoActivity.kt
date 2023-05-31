package com.shangyun.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.business.vm.PersonalInfoViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.databinding.ActivityPersonalInfoBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.activity.common.UpdateValueActivity
import com.shangyun.haile_manager_android.ui.activity.subAccount.SubAccountCreateActivity
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.shangyun.haile_manager_android.utils.DialogUtils

class PersonalInfoActivity :
    BaseBusinessActivity<ActivityPersonalInfoBinding, PersonalInfoViewModel>(PersonalInfoViewModel::class.java) {

    override fun layoutId(): Int = R.layout.activity_personal_info

    override fun backBtn(): View = mBinding.barPersonalInfoTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.llPersonalInfoHead.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this@PersonalInfoActivity)
        }
        mBinding.tvPersonalInfoName.setOnClickListener {
            startActivity(Intent(this@PersonalInfoActivity, UpdateValueActivity::class.java))
        }
    }

    override fun initData() {
        mBinding.shared = mSharedViewModel
    }
}