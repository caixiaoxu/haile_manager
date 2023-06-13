package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.BalanceTotalEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityWalletBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletActivity : BaseBindingActivity<ActivityWalletBinding>() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    private var balanceTotal: BalanceTotalEntity? = null

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


        mBinding.btnWalletWithdraw.setOnClickListener {
            if (!IntentParams.WalletParams.parseRealNameAuthStatus(intent)) {
                SToast.showToast(this@WalletActivity, R.string.err_no_real_name_auth)
                return@setOnClickListener
            }
            startActivity(Intent(this@WalletActivity, WalletWithdrawActivity::class.java).apply {
                putExtras(
                    IntentParams.WalletWithdrawParams.pack(balanceTotal?.totalAmount ?: "")
                )
            })
        }

        mBinding.btnWalletCharge.setOnClickListener {
            if (!IntentParams.WalletParams.parseRealNameAuthStatus(intent)) {
                SToast.showToast(this@WalletActivity, R.string.err_no_real_name_auth)
                return@setOnClickListener
            }
            startActivity(Intent(this@WalletActivity, RechargeActivity::class.java))
        }

        initEvent()
        initData()
    }

    private fun initEvent() {
        LiveDataBus.with(BusEvents.BALANCE_STATUS)?.observe(this) {
            initData()
        }
    }

    private fun initData() {
        launch({
            ApiRepository.dealApiResult(mCapitalRepo.requestBalance())?.let {
                balanceTotal = it
                withContext(Dispatchers.Main) {
                    mBinding.tvWalletMoney.text = it.totalAmount
                }
            }
        })
    }
}