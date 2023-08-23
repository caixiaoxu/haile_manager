package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
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
class WithdrawBindAlipayViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var id: Int = -1

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

    val alipayAccount: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val alipayName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(alipayAccount) {
            value = checkSubmit()
        }
        addSource(alipayName) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !alipayAccount.value.isNullOrEmpty() && !alipayName.value.isNullOrEmpty()

    fun checkSms() {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.checkCashOutOperateSms(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "verifyCode" to verifyCode.value!!
                        )
                    )
                )
            )?.let {
                authCode.postValue(it.authCode)
            }
        })
    }

    fun bindAlipayAccount(v: View) {
        launch({
            val params = hashMapOf<String, Any>(
                "authCode" to authCode.value!!,
                "cashOutAccount" to alipayAccount.value!!,
                "realName" to alipayName.value!!,
            )
            ApiRepository.dealApiResult(
                if (id > 0) {
                    mCapitalRepo.updateWithdrawAlipayAccount(
                        ApiRepository.createRequestBody(params.apply {
                            this["id"] = id
                        })
                    )
                } else {
                    mCapitalRepo.bindWithdrawAlipayAccount(
                        ApiRepository.createRequestBody(params)
                    )
                }
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(
                    v.context,
                    if (id > 0) R.string.update_success else R.string.bind_success
                )
            }
            LiveDataBus.post(BusEvents.BIND_WITHDRAW_ACCOUNT_STATUS, true)
            jump.postValue(0)
        })
    }
}