package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.BankCardDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/11 11:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BankCardDetailViewModel : BaseViewModel() {
    private val mCapitalService = ApiRepository.apiClient(CapitalService::class.java)

    var cardId: Int = -1

    val bankCardDetail: MutableLiveData<BankCardDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (0 >= cardId) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalService.requestBankCardDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to cardId
                        )
                    )
                )
            )?.let {
                bankCardDetail.postValue(it)
            }
        })
    }

    /**
     * 删除银行卡
     */
    fun deleteBankCard(authCode: String, callBack: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalService.requestBankCardDelete(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "authCode" to authCode,
                            "id" to cardId
                        )
                    )
                )
            )

            LiveDataBus.post(BusEvents.BANK_LIST_DELETE_STATUS, true)
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }

    /**
     * 解绑银行卡
     */
    fun releaseBankCard(authCode: String, callBack: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalService.requestBankCardRelease(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "authCode" to authCode,
                            "id" to cardId
                        )
                    )
                )
            )

            LiveDataBus.post(BusEvents.BANK_LIST_DELETE_STATUS, true)
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }
}