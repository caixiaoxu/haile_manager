package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.entities.ShopOperationSettingEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/4 18:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopOperationSettingViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    var shopId: Int = -1

    val operationSettingDetail: MutableLiveData<ShopOperationSettingEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == shopId) return
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.requestOperationSettingDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf("id" to shopId)
                    )
                )
            )?.let {
                operationSettingDetail.postValue(it.apply {
                    showItem1 = null != paymentSetting
                    showItem2 = null != compensationSetting
                    showItem3 = null != appointSetting
                    showItem4 = null != operationSetting

                    appointSetting?.settingList = appointSetting?.settings
                    appointSetting?.settings = null
                })
            }
        })
    }
}