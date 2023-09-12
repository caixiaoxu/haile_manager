package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.BalanceTotalEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityWalletBinding
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
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

        val authInfo = IntentParams.WalletParams.parseRealNameAuthStatus(intent)
        mBinding.btnWalletWithdraw.setOnClickListener {
            if (3 != authInfo?.status) {
                CommonDialog.Builder(StringUtils.getString(R.string.withdraw_err_no_real_name_auth))
                    .apply {
                        title = StringUtils.getString(R.string.tip)
                        negativeTxt = StringUtils.getString(R.string.cancel)
                        setPositiveButton("去认证") {
                            startActivity(
                                if (1 == authInfo?.status) {
                                    Intent(
                                        this@WalletActivity,
                                        BindSmsVerifyActivity::class.java
                                    ).apply {
                                        putExtras(IntentParams.BindSmsVerifyParams.pack(2))
                                    }
                                } else {
                                    Intent(
                                        this@WalletActivity,
                                        RealNameAuthActivity::class.java
                                    ).apply {
                                        authInfo?.let {
                                            putExtras(IntentParams.RealNameAuthParams.pack(it))
                                        }
                                    }
                                }
                            )
                        }
                    }.build()
                    .show(supportFragmentManager)
                return@setOnClickListener
            }
            startActivity(Intent(this@WalletActivity, WalletWithdrawActivity::class.java).apply {
                putExtras(
                    IntentParams.WalletWithdrawParams.pack(balanceTotal?.totalAmount ?: "")
                )
            })
        }

        mBinding.btnWalletCharge.setOnClickListener {
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