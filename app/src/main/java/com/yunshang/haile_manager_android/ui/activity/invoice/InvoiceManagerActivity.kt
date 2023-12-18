package com.yunshang.haile_manager_android.ui.activity.invoice

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.ActivityInvoiceManagerBinding

class InvoiceManagerActivity : BaseBindingActivity<ActivityInvoiceManagerBinding>() {

    override fun layoutId(): Int = R.layout.activity_invoice_manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE

        // 提现手续费
        mBinding.tvInvoiceManagerWithdrawFee.setOnClickListener {
            startActivity(
                Intent(
                    this@InvoiceManagerActivity,
                    InvoiceWithdrawFeeActivity::class.java
                )
            )
        }

        // 发票抬头
        mBinding.tvInvoiceManagerTitle.setOnClickListener {
            startActivity(
                Intent(
                    this@InvoiceManagerActivity,
                    InvoiceTitleActivity::class.java
                )
            )
        }

        // 发票历史
        mBinding.tvInvoiceManagerHistory.setOnClickListener {
            startActivity(
                Intent(
                    this@InvoiceManagerActivity,
                    InvoiceHistoryActivity::class.java
                )
            )
        }

        // 发票说明
        mBinding.tvInvoiceManagerExplain.setOnClickListener {
            startActivity(
                Intent(
                    this@InvoiceManagerActivity,
                    InvoiceExplainActivity::class.java
                )
            )
        }
    }
}