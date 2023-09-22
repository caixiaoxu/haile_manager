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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RegisterViewModel
import com.yunshang.haile_manager_android.data.ActivityTag
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityRegisterBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.AreaSelectDialog
import com.yunshang.haile_manager_android.web.WebViewActivity

class RegisterActivity : BaseBusinessActivity<ActivityRegisterBinding, RegisterViewModel>(
    RegisterViewModel::class.java, BR.vm
) {

    // 区域选择
    private val mAreaDialog: AreaSelectDialog by lazy {
        AreaSelectDialog.Builder().apply {
            onAreaSelect = { province, city, distract ->
                mViewModel.registerParams.provinceId = province.areaId
                mViewModel.registerParams.cityId = city.areaId
                mViewModel.registerParams.districtId = distract.areaId
                mViewModel.registerParams.areaVal =
                    province.areaName + city.areaName + distract.areaName
            }
        }.build()
    }

    override fun layoutId(): Int = R.layout.activity_register

    override fun backBtn(): View = mBinding.barRegisterTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.jump.observe(this) {
            AppManager.finishAllActivityForTag(ActivityTag.TAG_LOGIN)
            ActivityCompat.startActivity(
                this@RegisterActivity,
                Intent(this@RegisterActivity, LoginForPasswordActivity::class.java).apply {
                    putExtras(IntentParams.LoginParams.pack(mViewModel.registerParams.phone))
                },
                null
            )
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

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

        //发送验证码
        mBinding.itemRegisterCode.mTailView.apply {
            setTextColor(
                ContextCompat.getColor(this@RegisterActivity, R.color.common_txt_color)
            )
        }.setOnClickListener {
            mViewModel.sendMsg(it)
        }

        mBinding.itemRegisterAffiliationArea.onSelectedEvent = {
            mAreaDialog.show(supportFragmentManager)
        }
    }

    override fun initData() {
    }
}