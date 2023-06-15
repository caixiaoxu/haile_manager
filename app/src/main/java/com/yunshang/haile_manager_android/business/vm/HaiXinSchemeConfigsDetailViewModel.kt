package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.entities.SchemeConfigsDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/14 19:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HaiXinSchemeConfigsDetailViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    val schemeDetail: MutableLiveData<SchemeConfigsDetailEntity> by lazy {
        MutableLiveData()
    }

    val rechargeQrCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    var shopId: Int = -1

    var shopName: String = ""

    fun requestData() {
        if (-1 == shopId) return
        launch({
            requestSchemeDetail()
            ApiRepository.dealApiResult(mHaiXinRepo.requestRechargeQrCode(shopId))?.let {
                rechargeQrCode.postValue(it)
            }
        })
    }

    private suspend fun requestSchemeDetail() {
        ApiRepository.dealApiResult(
            mHaiXinRepo.requestSchemeDetail(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "shopId" to shopId
                    )
                )
            )
        )?.let {
            schemeDetail.postValue(it)
        }
    }

    fun switchSchemeOpen(isCheck: Boolean) {
        schemeDetail.value?.let {
            val paramsBody = ApiRepository.createRequestBody(
                hashMapOf("id" to it.id)
            )
            launch({
                ApiRepository.dealApiResult(
                    if (isCheck) {
                        mHaiXinRepo.openSchemeConfigs(paramsBody)
                    } else {
                        mHaiXinRepo.closeSchemeConfigs(paramsBody)
                    }
                )
                requestSchemeDetail()
            })
        }
    }
}