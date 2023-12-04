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
import com.yunshang.haile_manager_android.data.extend.isGreaterThan0
import com.yunshang.haile_manager_android.databinding.ActivityWalletWithdrawBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.loadImage
import com.yunshang.haile_manager_android.ui.view.dialog.WithdrawDialog
import com.yunshang.haile_manager_android.utils.GlideUtils

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
        mViewModel.accountFrozen =
            IntentParams.WalletWithdrawParams.parseAccountFrozen(intent)?.trim() ?: ""
        mViewModel.withDrawType = IntentParams.WalletWithdrawParams.parseWithdrawType(intent)
            .let { if (it.isGreaterThan0()) it else null }
    }

    override fun initEvent() {
        super.initEvent()
        LiveDataBus.with(BusEvents.BIND_WITHDRAW_ACCOUNT_STATUS)?.observe(this) {
            mViewModel.requestBindAccount()
        }

        mViewModel.withdrawAccount.observe(this) {
            // 图标
            if (1 == it.cashOutType) {
                mBinding.ivWalletWithdrawAlipayAccount.loadImage(R.mipmap.icon_withdraw_alipay)
            } else {
                GlideUtils.loadImage(
                    mBinding.ivWalletWithdrawAlipayAccount,
                    it.icon,
                    default = R.mipmap.icon_bank_main_default
                )
            }

            // 提示
            mBinding.tvWalletWithdrawAlipayAccount.setHint(if (1 == it.cashOutType) R.string.empty_alipay_account_hint else R.string.empty_bank_account_hint)
            // 内容
            mBinding.tvWalletWithdrawAlipayAccount.text = if (true == it.exist) {
                "${if (1 == it.cashOutType) "支付宝账号" else (it.bank ?: "其他银行")}（${
                    it.cashOutAccount?.let { outAccount ->
                        if (outAccount.length > 4) {
                            outAccount.substring(outAccount.length - 4)
                        } else {
                            outAccount
                        }
                    } ?: ""
                }）"
            } else ""
        }

        mViewModel.withdrawAmount.observe(this) {
            mViewModel.withdrawErr.value = try {
                val amount = it.toDouble()
                val balanceTotal = mViewModel.balanceTotal.toDouble()
                val minAmount = mViewModel.withdrawAccount.value!!.minWithdrawAmount!!.toDouble()
                val maxAmount = mViewModel.withdrawAccount.value!!.maxWithdrawAmount!!.toDouble()
                if (!it.isNullOrEmpty() && amount < minAmount) {
                    "提现金额不能小于${minAmount}元"
                } else if (amount > balanceTotal) {
                    "输入金额超过可提现金额"
                } else if (balanceTotal > maxAmount && amount > maxAmount) {
                    val maxVal =
                        (if (maxAmount > 10000) maxAmount / 10000 else maxAmount).let { max ->
                            if ((max * 100).toInt() % 100 == 0) max.toInt() else max
                        }
                    "每日可提现额度上限为${if (maxAmount > 10000) "${maxVal}万" else "$maxVal"}元"
                } else ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
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
            // 只有支付宝可修改
            if (1 == mViewModel.withdrawAccount.value?.cashOutType) {
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
            } else {
                // 没有银行卡跳转到银行卡列表
                if (true != mViewModel.withdrawAccount.value?.exist) {
                    startActivity(
                        Intent(
                            this@WalletWithdrawActivity,
                            BankCardActivity::class.java
                        )
                    )
                }
            }
        }

        mBinding.tvWalletWithdrawAmountAll.setOnClickListener {
            try {
                val balanceTotal = mViewModel.balanceTotal.toDouble()
                val maxAmount = mViewModel.withdrawAccount.value!!.maxWithdrawAmount!!.toDouble()
                selectAllAmount(if (balanceTotal > maxAmount) mViewModel.withdrawAccount.value!!.maxWithdrawAmount!! else mViewModel.balanceTotal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun selectAllAmount(amount: String) {
        if (amount.isNotEmpty()) {
            mViewModel.withdrawAmount.postValue(amount)
        }
    }

    override fun initData() {
        mViewModel.requestBindAccount()
    }
}