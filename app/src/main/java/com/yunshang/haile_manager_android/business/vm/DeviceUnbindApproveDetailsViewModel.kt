package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.DeviceUnbindApproveDetails
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/16 18:12
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindApproveDetailsViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    var approveId: Int? = -1

    var approveDetails by mutableStateOf<DeviceUnbindApproveDetails?>(null)

    fun requestData() {
        if (!approveId.hasVal()) return

        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.requestDeviceUnbindApproveDetails(approveId!!)
            )?.let {
                approveDetails = it
            }
        })
    }

    fun disposeDeviceUnbind(context: Context, operateType: Int) {
        if (!approveId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.approveDeviceUnbind(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to approveId,
                            "operateType" to operateType,
                        )
                    )
                )
            )
            requestData()
            LiveDataBus.post(BusEvents.DEVICE_UNBIND_APPROVE_STATUS, true)
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.operate_success)
            }
        })
    }
}