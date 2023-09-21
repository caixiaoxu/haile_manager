package com.yunshang.haile_manager_android.ui.activity.login

import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RegisterViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityRegisterBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.web.WebViewActivity

class RegisterActivity : BaseBusinessActivity<ActivityRegisterBinding, RegisterViewModel>(
    RegisterViewModel::class.java, BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_register

    override fun backBtn(): View = mBinding.barRegisterTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE
        mBinding.shared = mSharedViewModel

        // 协议内容
        mBinding.tvRegisterAgreement.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvRegisterAgreement.highlightColor = Color.TRANSPARENT
        mBinding.tvRegisterAgreement.text =
            SpannableString(StringUtils.getString(R.string.register_agreement_hint)).apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.colorPrimary
                        )
                    ),
                    6,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                // 隐私协议
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(view: View) {
                            startActivity(
                                Intent(
                                    this@RegisterActivity,
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
                    }, 6,
                    16,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                // 服务协议
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(view: View) {
                            startActivity(
                                Intent(
                                    this@RegisterActivity,
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
                    }, 18,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

        mBinding.itemRegisterCode.mTailView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.common_txt_color
            )
        )
        //发送验证码
        mBinding.itemRegisterCode.mTailView.setOnClickListener {

        }
    }

    override fun initData() {
    }
}