package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityWalletBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class WalletActivity : BaseBindingActivity<ActivityWalletBinding>() {

    override fun layoutId(): Int = R.layout.activity_wallet

    override fun backBtn(): View = mBinding.barWalletTitle.getBackBtn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.barWalletTitle.getRightBtn().run {
            setText(R.string.balance_detail)
            setTextColor(ContextCompat.getColor(this@WalletActivity, R.color.colorPrimary))
            textSize = 14f
            setOnClickListener {
                startActivity(Intent(this@WalletActivity, BalanceActivity::class.java))
            }
        }

        mBinding.tvWalletMoney.text = IntentParams.WalletParams.parseTotalBalance(intent) ?: ""

        mBinding.btnWalletWithdraw.setOnClickListener {
            if (!IntentParams.WalletParams.parseRealNameAuthStatus(intent)){
                SToast.showToast(this@WalletActivity,R.string.err_no_real_name_auth)
                return@setOnClickListener
            }
        }
    }
}