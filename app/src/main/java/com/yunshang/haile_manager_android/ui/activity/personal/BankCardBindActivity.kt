package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.BankCardBindViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.BankCardDetailEntity
import com.yunshang.haile_manager_android.databinding.ActivityBankCardBindBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class BankCardBindActivity :
    BaseBusinessActivity<ActivityBankCardBindBinding, BankCardBindViewModel>(
        BankCardBindViewModel::class.java, BR.vm
    ) {

    // 跳转短信验证界面
    private val startSms =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { resultData ->
                    IntentParams.WithdrawBindAlipayParams.parseAuthCode(resultData)
                        ?.let { authCode ->
                            mViewModel.authCode = authCode
                            nextOrSubmit()
                        }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_bank_card_bind

    override fun backBtn(): View = mBinding.barBankCardBindTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
        mViewModel.authCode = IntentParams.WithdrawBindAlipayParams.parseAuthCode(intent)
        mViewModel.authInfo.value = IntentParams.RealNameAuthParams.parseAuthInfo(intent)
        mViewModel.bankCardParams.value =
            IntentParams.BinkCardBindParams.parseBankCardDetail(intent) ?: BankCardDetailEntity()

    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.bindPage.observe(this) {
            it?.let {
                changePage(it)
            }
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    private fun changePage(page: Int) {
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }

        val fragment = mViewModel.fragments[page]
        val tag = fragment::class.java.simpleName
        supportFragmentManager.findFragmentByTag(tag)?.let {
            supportFragmentManager.beginTransaction().show(fragment).commit()
        } ?: run {
            supportFragmentManager.beginTransaction()
                .add(R.id.fl_bank_card_bind_control, fragment, tag)
                .show(fragment).commit()
        }
    }

    override fun onBackListener() {
        if (1 == mViewModel.bindPage.value) {
            mViewModel.bindPage.value = 0
        } else {
            super.onBackListener()
        }
    }

    override fun initView() {
        mBinding.btnBankCardSubmit.setOnClickListener {
            nextOrSubmit()
        }
    }

    private fun nextOrSubmit() {
        mViewModel.nextOrSubmit(this@BankCardBindActivity) { success, msg ->
            CommonDialog.Builder(msg).apply {
                title = StringUtils.getString(R.string.tip)
                isCancelable = false
                isNegativeShow = !success
                negativeTxt = StringUtils.getString(R.string.cancel)
                if (!success) {
                    setPositiveButton("获取授权码") {
                        startSms.launch(
                            Intent(
                                this@BankCardBindActivity,
                                BindSmsVerifyActivity::class.java
                            ).apply {
                                putExtras(IntentParams.BindSmsVerifyParams.pack(6, true))
                            })
                    }
                } else {
                    setPositiveButton("确定返回") {
                        finish()
                    }
                }
            }.build().show(supportFragmentManager)
        }
    }

    override fun initData() {
    }
}