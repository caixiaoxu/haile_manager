package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.WithdrawAccountEntity
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
    
    val withdrawAmount:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun requestData() {
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

    fun sendWithdrawOperateSms(callBack:(isSuccess:Boolean)->Unit){
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.sendCashOutOperateSms(
                    ApiRepository.createRequestBody("")
                )
            )?.let {
                withContext(Dispatchers.Main){
                    callBack(true)
                }
            }
        })
    }
}