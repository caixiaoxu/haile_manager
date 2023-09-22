package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnClickListener
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.lsy.framelib.ui.base.activity.BaseActivity
import com.lsy.framelib.utils.ActivityUtils
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.ActivityTag
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityLoginBinding
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.web.WebViewActivity

class LoginActivity : BaseActivity() {

    private val mLoginBinding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    //隐私协议同意弹窗
    private val mAgreementDialog: CommonDialog by lazy {
        CommonDialog.Builder(
            StringUtils.formatMultiStyleStr(
                getString(R.string.agreement_hint),
                arrayOf(
                    ForegroundColorSpan(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimary,
                            null
                        )
                    ),
                    object : ClickableSpan() {
                        override fun onClick(view: View) {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    WebViewActivity::class.java
                                ).apply {
                                    putExtras(
                                        IntentParams.WebViewParams.pack(
                                            BuildConfig.PRIVACY_POLICY,
                                        )
                                    )
                                })
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            //去掉下划线
                            ds.isUnderlineText = false
                        }
                    },
                ), 17, 23
            )
        ).apply {
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
        initStatusBarTxtColor(mLoginBinding.root)
        // 双击回退
        ActivityUtils.addDoubleBack(this, onBackPressedDispatcher)
        //协议
        showAgreement()

        // 跳转密码登录界面
        mLoginBinding.btnLoginForPassword.setOnClickListener {
            ActivityCompat.startActivity(
                this@LoginActivity,
                Intent(this@LoginActivity, LoginForPasswordActivity::class.java).apply {
                    SPRepository.changeUser?.lastOrNull()?.let { user ->
                        putExtras(IntentParams.LoginParams.pack(user.userInfo.userInfo.phone))
                    }
                },
                null
            )
        }
        // 跳转手机登录界面
        mLoginBinding.btnLoginForPhone.setOnClickListener {
            ActivityCompat.startActivity(
                this@LoginActivity,
                Intent(this@LoginActivity, LoginForPhoneActivity::class.java).apply {
                    SPRepository.changeUser?.lastOrNull()?.let { user ->
                        putExtras(IntentParams.LoginParams.pack(user.userInfo.userInfo.phone))
                    }
                },
                null
            )
        }

        mLoginBinding.btnLoginRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
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