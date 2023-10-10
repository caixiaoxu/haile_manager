package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.ShopPositionDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/10 15:21
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopPositionDetailViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    var positionId: Int? = null

    val positionDetail: MutableLiveData<ShopPositionDetailEntity> by lazy {
        MutableLiveData()
    }

    /**
     * 请求点位详情
     */
    fun requestPositionDetail() {
        if (null == positionId) return
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.requestPositionDetail(positionId!!)
            )?.let {
                positionDetail.postValue(it)
            }
        })
    }

    /**
     * 修改点位状态
     * @param state 要改变的状态
     */
    fun changePositionState(state: Int) {
        if (null == positionId) return
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.updatePositionState(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to positionId,
                            "state" to state,
                        )
                    )
                )
            )
            positionDetail.value?.stateVal = state
        })
    }

    /**
     * 删除点位
     */
    fun deletePosition() {
        if (null == positionId) return
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.deletePosition(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to positionId
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)
            jump.postValue(0)
        })
    }
}