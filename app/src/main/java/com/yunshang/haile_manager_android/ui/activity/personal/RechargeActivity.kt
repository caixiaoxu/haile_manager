package com.yunshang.haile_manager_android.ui.activity.personal

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.alipay.sdk.app.PayTask
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.RechargeViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.AliPayResultBean
import com.yunshang.haile_manager_android.data.entities.WxPrePayEntity
import com.yunshang.haile_manager_android.databinding.ActivityRechargeBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.utils.WeChatHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class RechargeActivity : BaseBusinessActivity<ActivityRechargeBinding, RechargeViewModel>(
    RechargeViewModel::class.java, BR.vm
) {

    override fun layoutId(): Int = R.layout.activity_recharge

    override fun backBtn(): View = mBinding.barRechargeTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.payParams.observe(this) {
            launch({
                if (103 == mViewModel.payWay.value) {
                    val alipay = PayTask(this@RechargeActivity)
                    val result = alipay.payV2(it, true)
                    Timber.i(result.toString())
                    withContext(Dispatchers.Main) {
                        val payResult = AliPayResultBean(result)
                        val resultStatus: String? = payResult.resultStatus
                        //用户取消不去请求接口查询支付状态
                        if (TextUtils.equals(resultStatus, "6001")) {
                            return@withContext
                        }
                        rechargeSuccess()
                    }
                } else if (203 == mViewModel.payWay.value) {
                    GsonUtils.json2Class(it, WxPrePayEntity::class.java)?.let { wxPrePayBean ->
                        WeChatHelper.openWeChatPay(
                            wxPrePayBean.appId,
                            wxPrePayBean.parentId,
                            wxPrePayBean.prepayId,
                            wxPrePayBean.nonceStr,
                            wxPrePayBean.timeStamp,
                            wxPrePayBean.paySign
                        )
                    }
                }
            })
        }
        LiveDataBus.with(BusEvents.WXPAY_STATUS)?.observe(this){
            rechargeSuccess()
        }
    }

    private fun rechargeSuccess() {
        mViewModel.requestPaySync {
            LiveDataBus.post(BusEvents.BALANCE_STATUS, true)
            startActivity(
                Intent(
                    this@RechargeActivity,
                    RechargeSuccessActivity::class.java
                ).apply {
                    putExtras(IntentParams.RechargeSuccessParams.pack(mViewModel.amount.value!!.toString()))
                })
            finish()
        }
    }

    override fun initView() {
    }

    override fun initData() {
    }
}