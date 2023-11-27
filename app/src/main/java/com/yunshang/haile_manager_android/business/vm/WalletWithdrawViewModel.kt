package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.WithdrawAccountEntity
import com.yunshang.haile_manager_android.data.entities.WithdrawCalculateEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/12 16:04
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class WalletWithdrawViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var balanceTotal: String = ""
    var accountFrozen: String = ""

    var withDrawType: Int? = null

    val withdrawAccount: MutableLiveData<WithdrawAccountEntity> by lazy {
        MutableLiveData()
    }

    val withdrawErr: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val cashOutLimit: LiveData<String> = withdrawAccount.map { account ->
        val build = StringBuilder()
        if (3 == account.cashOutType) {
            try {
                val frozen = accountFrozen.toDouble()
                val frozenVal = (if (frozen > 10000) frozen / 10000 else frozen).let {
                    if ((it * 100).toInt() % 100 == 0) it.toInt() else it
                }
                build.append(
                    com.lsy.framelib.utils.StringUtils.getString(R.string.bank_withdraw_hint)
                )
                build.append(
                    "，" + com.lsy.framelib.utils.StringUtils.getString(
                        R.string.bank_withdraw_hint2,
                        if (frozen > 10000) "${frozenVal}万" else "$frozenVal"
                    ) + "\n"
                )
            } catch (e: Exception) {
                ""
            }
        }

        try {
            val max = account.maxWithdrawAmount!!.toDouble()
            val maxVal = (if (max > 10000) max / 10000 else max).let {
                if ((it * 100).toInt() % 100 == 0) it.toInt() else it
            }
            build.append(
                com.lsy.framelib.utils.StringUtils.getString(
                    R.string.alipay_withdraw_hint,
                    if (max > 10000) "${maxVal}万" else "$maxVal"
                )
            )
        } catch (e: Exception) {
            ""
        }
        build.toString()
    }

    val withdrawAmount: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(withdrawAmount) {
            value = checkSubmit()
        }
        addSource(withdrawErr) {
            value = checkSubmit()
        }
        addSource(withdrawAccount) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        null != withdrawAmount.value && null != withdrawAccount.value?.id && withdrawErr.value.isNullOrEmpty()

    val withdrawCalculate: MutableLiveData<WithdrawCalculateEntity> by lazy {
        MutableLiveData()
    }

    fun requestBindAccount() {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.requestWithdrawAccount(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "cashOutAccountType" to withDrawType
                        )
                    )
                )
            )?.let {
                withdrawAccount.postValue(it)
            }
        })
    }

    fun sendWithdrawOperateSms(callBack: (isSuccess: Boolean) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.sendCashOutOperateSms(
                    ApiRepository.createRequestBody("")
                )
            )
            withContext(Dispatchers.Main) {
                callBack(true)
            }
        })
    }

    fun submitWithdraw(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.calculateWithdraw(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "withdrawAmount" to withdrawAmount.value!!,
                            "cashOutAccountType" to withDrawType
                        )
                    )
                )
            )?.let {
                withdrawCalculate.postValue(it)
            }
        })
    }

    fun withdraw(amount: String, callBack: (isSuccess: Boolean) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.balanceWithdraw(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "withdrawAmount" to amount,
                            "cashOutAccountType" to withDrawType
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.BALANCE_STATUS, true)
            withContext(Dispatchers.Main) {
                callBack(true)
            }
        })
    }
}