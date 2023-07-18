package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.alipay.sdk.app.PayTask
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.BalanceRechargeEntity
import com.yunshang.haile_manager_android.data.entities.PrePayEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import timber.log.Timber


/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/13 16:53
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RechargeViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    val amount: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val payWayBtn: MutableLiveData<Int> = MutableLiveData(R.id.rb_recharge_alipay_way)

    val payWay: LiveData<Int> = payWayBtn.map {
        when (it) {
            R.id.rb_recharge_wechat_way -> 203
            else -> 103
        }
    }

    val payParams: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(amount) {
            value = checkSubmit()
        }
        addSource(payWay) {
            value = checkSubmit()
        }
    }

    private var orderNo: String? = null

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean = !amount.value.isNullOrEmpty() && try {
        amount.value!!.toDouble() > 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } && 0 != payWay.value!!

    fun payNow(v: View) {
        launch({
            requestPayOrderNo()?.let {
                if (it.orderNo.isEmpty()) {
                    throw CommonCustomException(-1, StringUtils.getString(R.string.err_request))
                }

                orderNo = it.orderNo

                requestPrePay()?.let { prePay ->
                    payParams.postValue(prePay.prepayParams)
                }
            }
        })
    }

    private suspend fun requestPayOrderNo(): BalanceRechargeEntity? {
        return ApiRepository.dealApiResult(
            mCapitalRepo.balanceRecharge(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "chargeAmount" to amount.value!!
                    )
                )
            )
        )
    }

    private suspend fun requestPrePay(): PrePayEntity? {
        if (null == orderNo) {
            return null
        }
        return ApiRepository.dealApiResult(
            mCapitalRepo.prePay(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "orderNo" to orderNo!!,
                        "payMethod" to payWay.value!!
                    )
                )
            )
        )
    }

    fun requestPaySync(callBack: () -> Unit) {
        if (null == orderNo) {
            return
        }
        launch({
            paySync(callBack)
        })
    }

    private suspend fun paySync(callBack: () -> Unit) {
        ApiRepository.dealApiResult(
            mCapitalRepo.paySync(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "orderNo" to orderNo!!,
                        "payMethod" to payWay.value!!
                    )
                )
            )
        )?.let {
            if (it.success) callBack() else paySync(callBack)
        } ?:  throw CommonCustomException(-1, StringUtils.getString(R.string.err_pay))
    }
}