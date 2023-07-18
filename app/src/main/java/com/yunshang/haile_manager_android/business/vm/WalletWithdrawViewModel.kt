package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
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

    val withdrawAccount: MutableLiveData<WithdrawAccountEntity> by lazy {
        MutableLiveData()
    }

    val withdrawAmount: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val canSubmit: LiveData<Boolean> = withdrawAmount.map {
        try {
            it.toDouble()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    val withdrawCalculate: MutableLiveData<WithdrawCalculateEntity> by lazy {
        MutableLiveData()
    }

    fun requestBindAccount() {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.requestWithdrawAccount(
                    ApiRepository.createRequestBody("")
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
                            "withdrawAmount" to withdrawAmount.value!!
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
                            "withdrawAmount" to amount
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