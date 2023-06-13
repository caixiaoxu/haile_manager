package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.View
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.VersionViewModel
import com.yunshang.haile_manager_android.databinding.ActivityVersionBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class VersionActivity : BaseBusinessActivity<ActivityVersionBinding, VersionViewModel>(
    VersionViewModel::class.java, BR.vm
) {
    override fun layoutId(): Int = R.layout.activity_version

    override fun backBtn(): View = mBinding.barVersionTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
    }

    override fun initData() {
    }
}