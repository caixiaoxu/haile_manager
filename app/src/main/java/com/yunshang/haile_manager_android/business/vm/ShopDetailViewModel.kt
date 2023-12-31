package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.ShopDetailEntity
import com.yunshang.haile_manager_android.data.entities.ShopOperationSettingEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopDetailViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    var shopId: Int = -1

    val shopDetail: MutableLiveData<ShopDetailEntity> by lazy {
        MutableLiveData()
    }

    val operationSettingDetail: MutableLiveData<ShopOperationSettingEntity> by lazy {
        MutableLiveData()
    }

    val showOperationSetting: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * 店铺详情
     */
    fun requestShopDetail() {
        if (-1 == shopId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mRepo.shopDetail(ApiRepository.createRequestBody(hashMapOf("id" to shopId)))
            )?.let {details->
                shopDetail.postValue(details)
            }

            ApiRepository.dealApiResult(
                mRepo.requestOperationSettingDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf("id" to shopId)
                    )
                )
            )?.let {
                operationSettingDetail.postValue(it.apply {
                    appointSetting?.settingList = appointSetting?.settings
                    appointSetting?.settings = null

                    showItem1 = null != paymentSetting
                    showItem2 = null != compensationSetting
                    showItem3 = !appointSetting?.settingList.isNullOrEmpty()
                    showItem4 = null != operationSetting
                    showItem5 = null != freeSelfClearSettingDetailDTO
                })
            }
        })
    }

    /**
     * 店铺删除
     */
    fun requestShopDelete() {
        if (-1 == shopId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mRepo.deleteShop(ApiRepository.createRequestBody(hashMapOf("id" to shopId)))
            )
            LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)
            jump.postValue(0)
        })
    }
}