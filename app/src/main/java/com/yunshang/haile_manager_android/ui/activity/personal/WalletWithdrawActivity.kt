package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.WalletWithdrawViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityWalletWithdrawBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.WithdrawDialog

class WalletWithdrawActivity :
    BaseBusinessActivity<ActivityWalletWithdrawBinding, WalletWithdrawViewModel>(
        WalletWithdrawViewModel::class.java, BR.vm
    ) {
    override fun layoutId(): Int = R.layout.activity_wallet_withdraw

    override fun backBtn(): View = mBinding.barWalletWithdrawTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        mViewModel.balanceTotal =
            IntentParams.WalletWithdrawParams.parseTotalBalance(intent)?.trim() ?: ""
    }

    override fun initEvent() {
        super.initEvent()
        LiveDataBus.with(BusEvents.BIND_WITHDRAW_ACCOUNT_STATUS)?.observe(this) {
            mViewModel.requestBindAccount()
        }

        mViewModel.withdrawAmount.observe(this) {
            try {
                if (it.toDouble() > mViewModel.balanceTotal.toDouble()) {
                    selectAllAmount()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mViewModel.withdrawCalculate.observe(this) { calculate ->
            if (null != calculate) {
                WithdrawDialog.Builder(calculate).apply {
                    positiveClickListener = OnClickListener {
                        mViewModel.withdraw(calculate.realAmount) { isTrue ->
                            if (isTrue) {
                                SToast.showToast(
                                    this@WalletWithdrawActivity,
                                    R.string.submit_success
                                )
                                finish()
                            }
                        }
                    }
                }.build().show(supportFragmentManager)
            }
        }
    }

    override fun initView() {
        mBinding.barWalletWithdrawTitle.getRightBtn().run {
            setText(R.string.withdraw_record)
            textSize = 14f
            setTextColor(
                ContextCompat.getColor(
                    this@WalletWithdrawActivity,
                    R.color.common_txt_color
                )
            )
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener {
                startActivity(
                    Intent(
                        this@WalletWithdrawActivity,
                        WithdrawRecordActivity::class.java
                    )
                )
            }
        }

        mBinding.viewWalletWithdrawAlipayAccount.setOnClickListener {
            mViewModel.sendWithdrawOperateSms {
                if (it) {
                    startActivity(
                        Intent(
                            this@WalletWithdrawActivity,
                            WithdrawBindAlipayActivity::class.java
                        ).apply {
                            mViewModel.withdrawAccount.value?.id?.let { id ->
                                putExtras(IntentParams.CommonParams.pack(id))
                            }
                        }
                    )
                }
            }
        }

        mBinding.tvWalletWithdrawAmountAll.setOnClickListener {
            selectAllAmount()
        }
    }

    private fun selectAllAmount() {
        if (mViewModel.balanceTotal.isNotEmpty()) {
            mViewModel.withdrawAmount.postValue(mViewModel.balanceTotal)
        }
    }

    override fun initData() {
        mViewModel.requestBindAccount()
    }
}