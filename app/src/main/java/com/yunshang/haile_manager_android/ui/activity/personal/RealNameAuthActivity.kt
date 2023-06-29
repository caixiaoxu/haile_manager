package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.RealNameAuthViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.databinding.ActivityRealNameAuthBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.DateSelectorDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.DialogUtils
import java.util.*

class RealNameAuthActivity :
    BaseBusinessActivity<ActivityRealNameAuthBinding, RealNameAuthViewModel>(
        RealNameAuthViewModel::class.java,
        BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_real_name_auth

    override fun backBtn(): View = mBinding.barRealNameAuthTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        val oldAuthInfo = IntentParams.RealNameAuthParams.parseAuthInfo(intent)
        if (null != oldAuthInfo) {
            mViewModel.isSubmit.postValue((null == oldAuthInfo.status || 1 == oldAuthInfo.status))
            mViewModel.authInfo.postValue(oldAuthInfo)
        } else {
            mViewModel.isSubmit.postValue(true)
            mViewModel.authInfo.postValue(RealNameAuthDetailEntity())
        }
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.isSubmit.observe(this) {
            mBinding.barRealNameAuthTitle.getRightArea().visibility =
                if (it) View.GONE else View.VISIBLE
        }
    }

    private fun initRightBtn() {
        mBinding.barRealNameAuthTitle.getRightBtn().run {
            setText(R.string.update_data)
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@RealNameAuthActivity, R.color.colorPrimary))
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener {
                mViewModel.isSubmit.postValue(true)
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        mBinding.itemRealNameAuthType.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                getString(
                    R.string.type
                ), mViewModel.verifyTypeList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<RealNameAuthViewModel.RealNameAuthVerifyType> {
                    override fun onValue(data: RealNameAuthViewModel.RealNameAuthVerifyType?) {
                        mViewModel.authInfo.value?.let {
                            it.verifyType = data?.id
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }

        // 身份证正面
        mBinding.ivIdCardFront.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this@RealNameAuthActivity, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mViewModel.uploadIdPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mViewModel.authInfo.value?.idCardFont = url
                        } else {
                            SToast.showToast(this@RealNameAuthActivity, R.string.upload_failure)
                        }
                    }
                }
            }
        }

        // 身份证反面
        mBinding.ivIdCardBack.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this@RealNameAuthActivity, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {

                    mViewModel.uploadIdPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mViewModel.authInfo.value?.idCardReverse = url
                        } else {
                            SToast.showToast(this@RealNameAuthActivity, R.string.upload_failure)
                        }
                    }
                }
            }
        }

        // 营业执照
        mBinding.ivBusinessLicense.setOnClickListener {
            DialogUtils.showImgSelectorDialog(this@RealNameAuthActivity, 1) { isSuccess, result ->
                if (isSuccess && !result.isNullOrEmpty()) {
                    mViewModel.uploadIdPhoto(result[0].cutPath) { isTrue, url ->
                        if (isTrue) {
                            mViewModel.authInfo.value?.companyLicense = url
                        } else {
                            SToast.showToast(this@RealNameAuthActivity, R.string.upload_failure)
                        }
                    }
                }
            }
        }

        mBinding.tvRealNameAuthIdCardIndateType.onSelectedEvent = {
            CommonBottomSheetDialog.Builder(
                getString(
                    R.string.indate_type
                ), mViewModel.inDateTypeList
            ).apply {
                onValueSureListener = object :
                    CommonBottomSheetDialog.OnValueSureListener<RealNameAuthViewModel.RealNameAuthVerifyType> {
                    override fun onValue(data: RealNameAuthViewModel.RealNameAuthVerifyType?) {
                        mViewModel.authInfo.value?.let {
                            it.idCardExpirationType = data?.id
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }
        mBinding.tvRealNameAuthIdCardIndate.onSelectedEvent = {
            DateSelectorDialog.Builder().apply {
                selectModel = 1
                onDateSelectedListener = object : DateSelectorDialog.OnDateSelectListener {
                    override fun onDateSelect(mode: Int, date1: Date, date2: Date?) {
                        date2?.let {
                            mViewModel.authInfo.value?.idCardExpirationDate =
                                "${DateTimeUtils.formatDateTime(date1, "yyyy-MM-dd")} - " +
                                        "${DateTimeUtils.formatDateTime(date2, "yyyy-MM-dd")}"
                        }
                    }
                }
            }.build().show(supportFragmentManager)
        }
    }

    override fun onBackListener() {
        mViewModel.authInfo.value?.status?.let { status ->
            if (status > 1 && mViewModel.isSubmit.value!!) {
                mViewModel.isSubmit.postValue(false)
            } else super.onBackListener()
        } ?: super.onBackListener()
    }

    override fun initData() {

    }
}