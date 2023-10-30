package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
class DropperTemperatureViewModel : BaseViewModel() {

    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    val imei: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val max: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val min: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

//    val temperatureSwitch: MutableLiveData<Boolean> by lazy {
//        MutableLiveData()
//    }


    /**
     * 提交语音设置
     */
    fun submit(view: View) {
        if (imei.value.isNullOrEmpty()) {
            return
        }
        var max_ = max.value!!
        var min_ = min.value!!

//        if (temperatureSwitch.value == true && (max_.isNullOrEmpty() || min_.isNullOrEmpty())) {
//            SToast.showToast(msg = "温度设置不能为空")
//            return
//        }

        var max_n: Int
        try {
            max_n = Integer.valueOf(max_)
        } catch (e: Exception) {
            e.printStackTrace()
            SToast.showToast(msg = "恢复温度格式不正确")
            return
        }
        if (max_n > 16 || max_n < -20) {
            SToast.showToast(msg = "恢复温度范围：-20至16度")
            return
        }
        var min_n: Int
        try {
            min_n = Integer.valueOf(min_)
        } catch (e: Exception) {
            e.printStackTrace()
            SToast.showToast(msg = "最低温度格式不正确")
            return
        }
        if (min_n > 15 || min_n < -20) {
            SToast.showToast(msg = "最低温度范围：-20至15度")
            return
        }
        if (min_n >= max_n) {
            SToast.showToast(msg = "恢复温度要高于最低温度")
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mRepo.deviceTemperatureSetting(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "imei" to imei.value!!,
                            "max" to max.value!!,
                            "min" to min.value!!,
                            "switch" to true,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                SToast.showToast(msg = "操作成功")
            }
            jump.postValue(0)
        })
    }
}