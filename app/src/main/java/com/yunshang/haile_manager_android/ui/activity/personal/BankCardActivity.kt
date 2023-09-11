package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.BankCardViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityBankCardBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog

class BankCardActivity : BaseBusinessActivity<ActivityBankCardBinding, BankCardViewModel>(
    BankCardViewModel::class.java, BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_bank_card

    override fun backBtn(): View = mBinding.barBankCardTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()
        mViewModel.bankCard.observe(this) {
            it?.let {
                val no = it.bankCardNo
                mBinding.tvBankCardNo.text =
                    com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                        no, arrayOf(
                            AbsoluteSizeSpan(DimensionUtils.sp2px(24f, this@BankCardActivity)),
                            StyleSpan(Typeface.BOLD),
                        ), no.length - 5, no.length
                    )
            }
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.BANK_LIST_STATUS)?.observe(this) {
            mViewModel.requestData()
        }

        // 监听删除
        LiveDataBus.with(BusEvents.BANK_LIST_DELETE_STATUS)?.observe(this) {
            mViewModel.bankCard.postValue(null)
        }
    }

    override fun initView() {
        mBinding.barBankCardTitle.getRightBtn(false).run {
            setText(R.string.settlement_explain)
            setTextColor(ContextCompat.getColor(this@BankCardActivity, R.color.colorPrimary))
            setOnClickListener {
                showSettlementInstrcuctionsDialog()
            }
        }

        val authInfo = IntentParams.RealNameAuthParams.parseAuthInfo(intent)
        mBinding.tvBankCardAdd.setOnClickListener {
            if (3 != authInfo?.status) {
                CommonDialog.Builder(StringUtils.getString(R.string.add_bank_card_no_real_name))
                    .apply {
                        title = StringUtils.getString(R.string.tip)
                        negativeTxt = StringUtils.getString(R.string.i_know)
                        setPositiveButton("去认证") {
                            startActivity(
                                Intent(
                                    this@BankCardActivity,
                                    RealNameAuthActivity::class.java
                                ).apply {
                                    authInfo?.let {
                                        putExtras(IntentParams.RealNameAuthParams.pack(it))
                                    }
                                }
                            )
                        }
                    }.build()
                    .show(supportFragmentManager)
                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this@BankCardActivity,
                    BindSmsVerifyActivity::class.java
                ).apply {
                    putExtras(intent)
                    putExtras(IntentParams.BindSmsVerifyParams.pack(1))
                }
            )
        }

        mBinding.clBankCard.setOnClickListener {
            mViewModel.bankCard.value?.let { card ->
                startActivity(Intent(this, BankCardDetailActivity::class.java).apply {
                    putExtras(IntentParams.CommonParams.pack(card.id))
                })
            }
        }
    }

    private fun showSettlementInstrcuctionsDialog() {
        CommonDialog.Builder(StringUtils.getString(R.string.settlement_explain_content)).apply {
            title = StringUtils.getString(R.string.settlement_explain)
            isCancelable = true
            isNegativeShow = false
            positiveTxt = StringUtils.getString(R.string.i_know)
        }.build().show(supportFragmentManager)
    }

    override fun initData() {
        mViewModel.requestData()
    }
}