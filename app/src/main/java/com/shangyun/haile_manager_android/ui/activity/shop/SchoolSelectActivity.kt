package com.shangyun.haile_manager_android.ui.activity.shop

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SchoolSelectViewModel
import com.shangyun.haile_manager_android.business.vm.ShopCreateViewModel
import com.shangyun.haile_manager_android.databinding.ActivitySchoolSelectBinding
import com.shangyun.haile_manager_android.databinding.ActivityShopCreateBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class SchoolSelectActivity :
    BaseBusinessActivity<ActivitySchoolSelectBinding, SchoolSelectViewModel>() {
    private val mSchoolSelectViewModel by lazy {
        getActivityViewModelProvider(this)[SchoolSelectViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_school_select

    override fun getVM(): SchoolSelectViewModel = mSchoolSelectViewModel

    override fun backBtn(): View = mBinding.schoolSelectTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

    }

    override fun initData() {
    }
}