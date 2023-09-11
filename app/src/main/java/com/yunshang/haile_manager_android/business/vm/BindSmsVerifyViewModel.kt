package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    val titleVal: LiveData<String> = verifyType.map {
        when (it) {
            0 -> StringUtils.getString(R.string.bind_alipay_account)
            1 -> StringUtils.getString(R.string.add_bank_card)
            2 -> StringUtils.getString(R.string.real_name)
            else -> ""
        }
    }

    val contentVal: LiveData<String> = verifyType.map {
        when (it) {
            0 -> StringUtils.getString(R.string.bind_alipay)
            1 -> StringUtils.getString(R.string.add_bank_card)
            2 -> StringUtils.getString(R.string.real_name)
            else -> ""
        }
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

    fun sendOperateSms(callback:(isSuccess:Boolean)->Unit) {
        launch({
            val body = ApiRepository.createRequestBody("")
            ApiRepository.dealApiResult(
                when (verifyType.value) {
                    0 -> mCapitalRepo.sendCashOutOperateSms(body)
                    1 -> mCapitalRepo.sendBankOperateSms(body)
                    else -> mCapitalRepo.sendRealNameOperateSms(body)
                }
            )
            withContext(Dispatchers.Main){
                callback(true)
            }
        },{
            withContext(Dispatchers.Main){
                callback(false)
            }
        })
    }

    fun checkSms() {
        launch({
            val body = ApiRepository.createRequestBody(
                hashMapOf(
                    "verifyCode" to verifyCode.value!!
                )
            )
            ApiRepository.dealApiResult(
                when (verifyType.value) {
                    0 -> mCapitalRepo.checkCashOutOperateSms(body)
                    1 -> mCapitalRepo.checkBankOperateSms(body)
                    else -> mCapitalRepo.checkRealNameOperateSms(body)
                }
            )?.let {
                authCode.postValue(it.authCode)
            }
        })
    }
}