package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.WalletWithdrawViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityWalletWithdrawBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

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
            }
        }

        mBinding.viewWalletWithdrawAlipayAccount.setOnClickListener {
            mViewModel.sendWithdrawOperateSms {
                if (it) {
                    startActivity(
                        Intent(
                            this@WalletWithdrawActivity,
                            WithdrawBindAlipayActivity::class.java
                        )
                    )
                }
            }
        }

        mBinding.tvWalletWithdrawAmountAll.setOnClickListener {
            if (mViewModel.balanceTotal.isNotEmpty()) {
                mViewModel.withdrawAmount.postValue(mViewModel.balanceTotal)
                mBinding.etWalletWithdrawAmount.setSelection(mViewModel.balanceTotal.length)
            }
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}