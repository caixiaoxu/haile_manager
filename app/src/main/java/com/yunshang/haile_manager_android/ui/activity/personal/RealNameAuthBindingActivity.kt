package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RealNameAuthBindingViewModel
import com.yunshang.haile_manager_android.databinding.ActivityRealNameAuthBindingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class RealNameAuthBindingActivity :
    BaseBusinessActivity<ActivityRealNameAuthBindingBinding, RealNameAuthBindingViewModel>(
        RealNameAuthBindingViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_real_name_auth_binding

    override fun backBtn(): View = mBinding.barRealNameAuthBindingTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }


}