package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.AppointmentSettingEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class AppointmentSettingViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    var shopId: Int = -1

    val appointmentSettingList: MutableLiveData<MutableList<AppointmentSettingEntity>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == shopId) return

        launch({
            val list = ApiRepository.dealApiResult(mRepo.getShopAppointmentSettingList(shopId))
            list?.let {
                appointmentSettingList.postValue(it)
            }
        })
    }

    /**
     * 保存
     */
    fun save(view: View) {
        if (appointmentSettingList.value.isNullOrEmpty()) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mRepo.setShopAppointment(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to shopId,
                            "settingList" to appointmentSettingList.value!!,
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}