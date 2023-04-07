package com.shangyun.haile_manager_android.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.app.ActivityCompat
import com.lsy.framelib.ui.base.BaseActivity
import com.lsy.framelib.utils.ActivityUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.ActivityTag
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.databinding.ActivityLoginBinding
import com.shangyun.haile_manager_android.ui.view.CommonDialog

class LoginActivity : BaseActivity() {

    private val mLoginBinding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    //隐私协议同意弹窗
    private val mAgreementDialog: CommonDialog by lazy {
        CommonDialog.Builder(getString(R.string.agreement_hint)).apply {
            positiveClickListener = OnClickListener {
                SPRepository.isAgreeAgreement = true
            }
        }.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mLoginBinding.root)
        initView()
    }

    override fun activityTag(): String = ActivityTag.TAG_LOGIN

    override fun backBtn(): View = mLoginBinding.loginTitleBar.getBackBtn()

    private fun initView() {
        initStatusBarColor(mLoginBinding.root)
        // 双击回退
        ActivityUtils.addDoubleBack(this, onBackPressedDispatcher)
        //协议
        showAgreement()

        // 跳转密码登录界面
        mLoginBinding.btnLoginForPassword.setOnClickListener {
            ActivityCompat.startActivity(
                this@LoginActivity,
                Intent(this@LoginActivity, LoginForPasswordActivity::class.java),
                null
            )
        }
        // 跳转手机登录界面
        mLoginBinding.btnLoginForPhone.setOnClickListener {
            ActivityCompat.startActivity(
                this@LoginActivity,
                Intent(this@LoginActivity, LoginForPhoneActivity::class.java),
                null
            )
        }
    }

    override fun onBackListener() {
        ActivityUtils.doubleBack(this)
    }

    /**
     * 显示协议
     */
    private fun showAgreement() {
        if (!SPRepository.isAgreeAgreement) {
            mAgreementDialog.show(supportFragmentManager)
        }
    }
}