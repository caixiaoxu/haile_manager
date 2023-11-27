package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.RealNameAuthViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityRealNameAuthBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.utils.GlideUtils
import java.util.*

class RealNameAuthActivity :
    BaseBusinessActivity<ActivityRealNameAuthBinding, RealNameAuthViewModel>(
        RealNameAuthViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_real_name_auth

    override fun backBtn(): View = mBinding.barRealNameAuthTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.authInfo.observe(this) {
            it?.let {
                if (1 != it.verifyType) {
                    loadRealNameAuthLicence()
                }
                loadRealNameAuthIdCardFront()
                loadRealNameAuthIdCardBack()
            }
        }

        LiveDataBus.with(BusEvents.REAL_NAME_AUTH_STATUS)?.observe(this){
            mViewModel.requestRealNameAuth()
        }
    }

    private fun initRightBtn() {
        mBinding.barRealNameAuthTitle.getRightBtn().run {
            setText(R.string.edit)
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@RealNameAuthActivity, R.color.colorPrimary))
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener {
                mViewModel.authInfo.value?.let {
                    startActivity(Intent(
                        this@RealNameAuthActivity,
                        BindSmsVerifyActivity::class.java
                    ).apply {
                        putExtras(IntentParams.RealNameAuthParams.pack(it))
                        putExtras(IntentParams.BindSmsVerifyParams.pack(2))
                    })
                }
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()
    }


    /**
     * 加载营业执照
     */
    private fun loadRealNameAuthLicence() {
        mViewModel.authInfo.value?.let { authInfo ->
            GlideUtils.glideDefaultFactory(
                mBinding.ivRealNameAuthCompanyLicensePic,
                authInfo.companyLicense
            ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                .into(mBinding.ivRealNameAuthCompanyLicensePic)
        }
    }

    /**
     * 加载身份证正面
     */
    private fun loadRealNameAuthIdCardFront() {
        mViewModel.authInfo.value?.let { authInfo ->
            GlideUtils.glideDefaultFactory(
                mBinding.ivRealNameAuthIdCardFront,
                authInfo.idCardFont
            ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                .into(mBinding.ivRealNameAuthIdCardFront)
        }
    }

    /**
     * 加载身份证反面
     */
    private fun loadRealNameAuthIdCardBack() {
        mViewModel.authInfo.value?.let { authInfo ->
            GlideUtils.glideDefaultFactory(
                mBinding.ivRealNameAuthIdCardBack,
                authInfo.idCardReverse
            ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                .into(mBinding.ivRealNameAuthIdCardBack)
        }
    }

    override fun initData() {
        IntentParams.RealNameAuthParams.parseAuthInfo(intent)?.let {
            mViewModel.authInfo.value = it
        } ?: mViewModel.requestRealNameAuth()
    }
}