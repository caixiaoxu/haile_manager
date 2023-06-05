package com.yunshang.haile_manager_android.ui.activity.common

import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.business.vm.UpdateValueViewModel
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityUpdateValueBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class UpdateValueActivity :
    BaseBusinessActivity<ActivityUpdateValueBinding, UpdateValueViewModel>(
        UpdateValueViewModel::class.java,
        BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_update_value

    override fun backBtn(): View = mBinding.barUpdateInfoTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.userInfo.observe(this) {
            it?.let { user ->
                mViewModel.updateVal.value = user.userInfo.name
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()
    }

    private fun initRightBtn() {
        mBinding.barUpdateInfoTitle.getRightBtn(false).run {
            setText(R.string.save)
            setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            setOnClickListener {
                mViewModel.updatePersonalName(it) {
                    mSharedViewModel.requestUserInfoAsync()
                    finish()
                }
            }
        }
    }

    override fun initData() {
    }
}