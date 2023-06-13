package com.yunshang.haile_manager_android.ui.activity.personal

import android.os.Bundle
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityRechargeSuccessBinding
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.ViewUtils

class RechargeSuccessActivity : BaseBindingActivity<ActivityRechargeSuccessBinding>() {
    override fun layoutId(): Int = R.layout.activity_recharge_success

    override fun backBtn(): View = mBinding.barRechargeSuccessTitle.getBackBtn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.tvRechargeSuccessAmount.text = StringUtils.formatMultiStyleStr(
            "¥ ${IntentParams.RechargeSuccessParams.parseAmount(intent)}",
            arrayOf(
                AbsoluteSizeSpan(DimensionUtils.sp2px(this@RechargeSuccessActivity, 18f))
            ), 0, 2
        )

        mBinding.btnRechargeSuccess.setOnClickListener {
            finish()
        }
    }
}