package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
class ShopFlowOperationSettingViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    var shopId: Int = -1

    val openSetting1: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun submit(openVal: Int, callback: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mRepo.saveOperationSetting(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to shopId,
                            "volumeVisibleState" to openVal
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            withContext(Dispatchers.Main){
                callback()
            }
        })
    }
}