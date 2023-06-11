package com.yunshang.haile_manager_android.ui.activity.personal

import androidx.databinding.library.baseAdapters.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RealNameAuthViewModel
import com.yunshang.haile_manager_android.databinding.ActivityRealNameAuthBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class RealNameAuthActivity :
    BaseBusinessActivity<ActivityRealNameAuthBinding, RealNameAuthViewModel>(RealNameAuthViewModel::class.java,BR.vm) {
    override fun layoutId(): Int =R.layout.activity_real_name_auth

    override fun initView() {
    }

    override fun initData() {
    }
}