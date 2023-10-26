package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.view.View
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.ChangePhoneLoginViewModel
import com.yunshang.haile_manager_android.data.extend.isGreaterThan0
import com.yunshang.haile_manager_android.databinding.ActivityChangeLoginPhoneBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class ChangeLoginPhoneActivity :
    BaseBusinessActivity<ActivityChangeLoginPhoneBinding, ChangePhoneLoginViewModel>(
        ChangePhoneLoginViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_change_login_phone

    override fun backBtn(): View = mBinding.barChangePhoneTitle.getBackBtn()

    override fun onBackListener() {
        if (mViewModel.step.value.isGreaterThan0()) {
            mViewModel.step.value = 0
            mViewModel.newPhone.value = ""
            mViewModel.step2Code.value = ""
        } else {
            super.onBackListener()
        }
    }

    override fun initView() {
        mBinding.tvChangePhoneStep2Code.setOnClickListener {
            mViewModel.sendStep2Code(it) { success, msg ->
                if (!success && !msg.isNullOrEmpty()) {
                    CommonDialog.Builder(msg).apply {
                        title = "提示"
                        positiveTxt = StringUtils.getString(R.string.i_know)
                        isNegativeShow = false
                    }.build().show(supportFragmentManager)
                }
            }
        }
        mBinding.btnChangePhoneStep2Sure.setOnClickListener {
            mViewModel.submitStep2(it) { success, msg ->
                if (success || !msg.isNullOrEmpty()) {
                    CommonDialog.Builder(if (success) "更换手机号成功，密码保持不变。请重新登录" else msg!!).apply {
                        if (success) {
                            title = "提示"
                            setPositiveButton("去登录") {
                                AppManager.finishAllActivity()
                                startActivity(
                                    Intent(
                                        this@ChangeLoginPhoneActivity,
                                        LoginActivity::class.java
                                    )
                                )
                            }
                        } else {
                            positiveTxt = StringUtils.getString(R.string.i_know)
                        }
                        isNegativeShow = false
                    }.build().show(supportFragmentManager)
                }
            }
        }
    }

    override fun initData() {
        mViewModel.oldPhone.value = mSharedViewModel.userInfo.value?.userInfo?.phone
    }
}