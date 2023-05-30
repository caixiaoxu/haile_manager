package com.shangyun.haile_manager_android.ui.activity.personal

import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.PersonalInfoViewModel
import com.shangyun.haile_manager_android.databinding.ActivityPersonalInfoBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class PersonalInfoActivity :
    BaseBusinessActivity<ActivityPersonalInfoBinding, PersonalInfoViewModel>(PersonalInfoViewModel::class.java) {

    override fun layoutId(): Int  = R.layout.activity_personal_info

    override fun initView() {
    }

    override fun initData() {
    }
}