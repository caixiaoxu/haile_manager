package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.AppointmentSettingEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
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
class AppointmentSettingViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    var shopId: Int = -1

    val appointmentSetting: MutableLiveData<AppointmentSettingEntity> =
        MutableLiveData(AppointmentSettingEntity())

    fun requestData() {
        if (-1 == shopId) return

        launch({
            val result = ApiRepository.dealApiResult(mRepo.getShopAppointmentSettingListV2(shopId))
            result?.let {
                appointmentSetting.postValue(result.apply {
                    settingList = settings
                    settings = null
                })
            }
        })
    }

    /**
     * 保存
     */
    fun save(view: View) {
        if (!shopId.hasVal() || null == appointmentSetting.value) {
            return
        }

        appointmentSetting.value?.shopId = shopId

        launch({
            ApiRepository.dealApiResult(
                mRepo.setShopAppointment(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(appointmentSetting.value)
                    )
                )
            )
            LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}