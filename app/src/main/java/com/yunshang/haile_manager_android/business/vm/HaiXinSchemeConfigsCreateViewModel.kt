package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.arguments.HaixinSchemeConfigCreateParams
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
class HaiXinSchemeConfigsCreateViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    var shopId: Int = -1
    val shopName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val createUpdateParams: MutableLiveData<HaixinSchemeConfigCreateParams> =
        MutableLiveData(HaixinSchemeConfigCreateParams())

    val reachVal:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val rewradVal:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun switchSchemeOpen(isCheck: Boolean) {
        createUpdateParams.value?.isAllowRefund = if (isCheck) 1 else 0
    }

    fun requestSchemeDetail() {
        if (-1 == shopId) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestSchemeDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to shopId
                        )
                    )
                )
            )?.let {
                createUpdateParams.postValue(HaixinSchemeConfigCreateParams().apply {
                    id = it.id
                    shopId = it.shopId
                    configName = it.configName
                    discountProportion = it.discountProportion
                    isForceUse = it.isForceUse
                    isAllowRefund = it.isAllowRefund
                    updateRewards = it.rewardsConfig
                    rewards = it.rewardsConfig
                })
            }
        })
    }
}