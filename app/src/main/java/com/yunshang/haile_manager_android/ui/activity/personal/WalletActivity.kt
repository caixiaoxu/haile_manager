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
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.BalanceTotalEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityWalletBinding
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
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

        mBinding.tvWalletTitle.setOnClickListener {
            CommonDialog.Builder(
                "我的余额=实时总收益-历史提现\n\n可提现余额（支付宝）：绑卡前，所有的支付都进入该余额。绑卡后，app支付支付和免密支付进入该余额。\n\n可提现余额（银行卡）：绑卡后，除app支付和免密支付之外的支付进入该余额"
            ).apply {
                title = "说明"
                isNegativeShow = false
                positiveTxt = StringUtils.getString(R.string.i_know)
            }.build().show(supportFragmentManager)
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
            val list = listOf(
                SearchSelectParam(0, "提现至支付宝"),
                SearchSelectParam(1, "提现至银行卡"),
            )
            CommonBottomSheetDialog.Builder(StringUtils.getString(R.string.wallet_withdraw), list)
                .apply {
                    showClose = false
                    isClickClose = true
                    onValueSureListener = object :
                        CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            startActivity(
                                Intent(
                                    this@WalletActivity,
                                    WalletWithdrawActivity::class.java
                                ).apply {
                                    putExtras(
                                        IntentParams.WalletWithdrawParams.pack(
                                            if (0 == data?.id) balanceTotal?.availableAmount else balanceTotal?.candyPayAvailableAmount,
                                            if (1 == data?.id) balanceTotal?.candyPayAvailableAmount else null,
                                            if (0 == data?.id) 1 else 3
                                        )
                                    )
                                })
                        }
                    }
                }.build().show(supportFragmentManager)
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
                    mBinding.tvWalletMoneyAvailableAmount.text =
                        StringUtils.getString(R.string.unit_money) + (it.availableAmount ?: "0")
                    mBinding.tvWalletMoneyCandyPayAmount.text =
                        StringUtils.getString(R.string.unit_money) + (it.candyPayAmount ?: "0")
                }
            }
        })
    }
}