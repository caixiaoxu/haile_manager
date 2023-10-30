package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SettingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivitySettingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.login.ChangeLoginPhoneActivity
import com.yunshang.haile_manager_android.ui.activity.login.ChangeUserActivity
import com.yunshang.haile_manager_android.ui.activity.login.LoginActivity
import com.yunshang.haile_manager_android.ui.activity.login.UpdateLoginPasswordActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.web.WebViewActivity

class SettingActivity : BaseBusinessActivity<ActivitySettingBinding, SettingViewModel>(
    SettingViewModel::class.java,
    BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_setting

    override fun backBtn(): View = mBinding.loginTitleBar.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvChangePassword.setOnClickListener {
            startActivity(Intent(this@SettingActivity, UpdateLoginPasswordActivity::class.java))
        }

        mBinding.tvChangeLoginPhone.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeLoginPhoneActivity::class.java))
        }

        mBinding.tvPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this@SettingActivity, WebViewActivity::class.java).apply {
                putExtras(
                    IntentParams.WebViewParams.pack(
                        BuildConfig.PRIVACY_POLICY,
                    )
                )
            })
        }

        mBinding.tvVersion.setOnClickListener {
            startActivity(Intent(this@SettingActivity, VersionActivity::class.java).apply {
                if (null != mViewModel.appVersionInfo.value) {
                    putExtras(IntentParams.VersionParams.pack(mViewModel.appVersionInfo.value!!))
                }
            })
        }

        mBinding.tvCancelAccount.setOnClickListener {
            CommonDialog.Builder(StringUtils.getString(R.string.cancel_account_hint)).apply {
                setNegativeButton(StringUtils.getString(R.string.cancel_account_sure)) {
                    mViewModel.cancelAccount {
                        SPRepository.cleaLoginUserInfo()
                        AppManager.finishAllActivity()
                        startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                    }
                }
                positiveTxt = StringUtils.getString(R.string.cancel_account_cancel)
            }.build()
                .show(supportFragmentManager)
        }

        // 联系平台
        mBinding.itemContactPlatform.onSelectedEvent = {
            startActivity(Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:${StringUtils.getString(R.string.platform_phone)}")
            })
        }

        mBinding.tvChangeAccount.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeUserActivity::class.java))
        }
    }

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            AppManager.finishAllActivity()
            startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
        }
        mViewModel.appVersionInfo.observe(this) {
            mBinding.tvSettingNewVersion.visibility =
                if (it.needUpdate || it.forceUpdate) View.VISIBLE else View.GONE
        }
    }

    override fun initData() {
        mViewModel.checkVersion(this)
    }
}