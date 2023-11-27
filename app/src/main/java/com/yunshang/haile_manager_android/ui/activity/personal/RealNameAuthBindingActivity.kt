package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.View
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RealNameAuthBindingViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.databinding.ActivityRealNameAuthBindingBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.DialogUtils
import com.yunshang.haile_manager_android.utils.GlideUtils
import java.util.*

class RealNameAuthBindingActivity :
    BaseBusinessActivity<ActivityRealNameAuthBindingBinding, RealNameAuthBindingViewModel>(
        RealNameAuthBindingViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_real_name_auth_binding

    override fun backBtn(): View = mBinding.barRealNameAuthBindingTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.authCode = IntentParams.WithdrawBindAlipayParams.parseAuthCode(intent)
        mViewModel.authInfo.value =
            IntentParams.RealNameAuthParams.parseAuthInfo(intent) ?: RealNameAuthDetailEntity()
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.authInfo.observe(this) {
            it?.let {
                loadRealNameAuthLicence()
                loadRealNameAuthIdCardFront()
                loadRealNameAuthIdCardBack()
            }
        }

        mViewModel.jump.observe(this) {
            finish()
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.itemRealNameAuthBindingType.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                getString(
                    R.string.type
                ), mViewModel.verifyTypeList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        mViewModel.authInfo.value?.verifyType = data?.id
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 营业执照
        mBinding.ivRealNameAuthBindingLicence.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mViewModel.uploadIdPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mViewModel.authInfo.value?.companyLicense = url
                            loadRealNameAuthLicence()
                        } else SToast.showToast(this, R.string.upload_failure)
                    }
                }
            }
        }

        // 身份证正面
        mBinding.ivRealNameAuthBindingIdCardFront.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mViewModel.uploadIdPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mViewModel.authInfo.value?.idCardFont = url
                            loadRealNameAuthIdCardFront()
                        } else SToast.showToast(this, R.string.upload_failure)
                    }
                }
            }
        }

        // 身份证反面
        mBinding.ivRealNameAuthBindingIdCardBack.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mViewModel.uploadIdPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mViewModel.authInfo.value?.idCardReverse = url
                            loadRealNameAuthIdCardBack()
                        } else SToast.showToast(this, R.string.upload_failure)
                    }
                }
            }
        }

        // 有效型类型
        mBinding.itemRealNameAuthIdCardIndateType.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                getString(
                    R.string.indate_type
                ), mViewModel.inDateTypeList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                    override fun onValue(data: SearchSelectParam?) {
                        mViewModel.authInfo.value?.idCardExpirationType = data?.id
                    }
                }
            }.build().show(supportFragmentManager)
        }

        mBinding.itemRealNameAuthIdCardIndate.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                selectModel = 1
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        date2?.let {
                            mViewModel.authInfo.value?.idCardExpirationDate =
                                "${
                                    DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")
                                },${DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd")}"
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }
    }

    /**
     * 加载营业执照
     */
    private fun loadRealNameAuthLicence() {
        mViewModel.authInfo.value?.let { authInfo ->
            GlideUtils.glideDefaultFactory(
                mBinding.ivRealNameAuthBindingLicence,
                authInfo.companyLicense,
                R.mipmap.bg_business_license
            ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                .into(mBinding.ivRealNameAuthBindingLicence)
        }
    }

    /**
     * 加载身份证正面
     */
    private fun loadRealNameAuthIdCardFront() {
        mViewModel.authInfo.value?.let { authInfo ->
            GlideUtils.glideDefaultFactory(
                mBinding.ivRealNameAuthBindingIdCardFront,
                authInfo.idCardFont,
                R.mipmap.bg_id_card_front
            ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                .into(mBinding.ivRealNameAuthBindingIdCardFront)
        }
    }

    /**
     * 加载身份证反面
     */
    private fun loadRealNameAuthIdCardBack() {
        mViewModel.authInfo.value?.let { authInfo ->
            GlideUtils.glideDefaultFactory(
                mBinding.ivRealNameAuthBindingIdCardBack,
                authInfo.idCardReverse,
                R.mipmap.bg_id_card_back
            ).transform(CenterCrop(), RoundedCorners(DimensionUtils.dip2px(this, 8f)))
                .into(mBinding.ivRealNameAuthBindingIdCardBack)
        }
    }

    override fun initData() {
    }
}