package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/12 18:08
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BindSmsVerifyViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    val verifyType: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val authCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val verifyCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val code1: LiveData<String> = verifyCode.map {
        if (!it.isNullOrEmpty()) it[0].toString() else ""
    }

    val code2: LiveData<String> = verifyCode.map {
        if (!it.isNullOrEmpty() && it.length > 1) it[1].toString() else ""
    }

    val code3: LiveData<String> = verifyCode.map {
        if (!it.isNullOrEmpty() && it.length > 2) it[2].toString() else ""
    }

    val code4: LiveData<String> = verifyCode.map {
        if (!it.isNullOrEmpty() && it.length > 3) it[3].toString() else ""
    }

    val code5: LiveData<String> = verifyCode.map {
        if (!it.isNullOrEmpty() && it.length > 4) it[4].toString() else ""
    }
    val code6: LiveData<String> = verifyCode.map {
        if (!it.isNullOrEmpty() && it.length > 5) it[5].toString() else ""
    }

    fun sendOperateSms() {
        launch({
            ApiRepository.dealApiResult(
                if (0 == verifyType.value){
                    mCapitalRepo.sendCashOutOperateSms(
                        ApiRepository.createRequestBody("")
                    )
                } else {
                    mCapitalRepo.sendBankOperateSms(
                        ApiRepository.createRequestBody("")
                    )
                }
            )
        })
    }

    fun checkSms() {
        launch({
            ApiRepository.dealApiResult(
                if (0 == verifyType.value){
                    mCapitalRepo.checkCashOutOperateSms(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "verifyCode" to verifyCode.value!!
                            )
                        )
                    )
                } else {
                    mCapitalRepo.checkBankOperateSms(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "verifyCode" to verifyCode.value!!
                            )
                        )
                    )
                }
            )?.let {
                authCode.postValue(it.authCode)
            }
        })
    }
}