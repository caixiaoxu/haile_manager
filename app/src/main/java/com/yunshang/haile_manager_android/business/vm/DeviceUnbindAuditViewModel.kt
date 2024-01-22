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
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/15 14:38
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindAuditViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    var goodId: Int? = -1

    var auditContent by mutableStateOf("")

    fun unbindAudit(context: Context) {
        if (!goodId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.unbindDeviceAudit(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsId" to goodId,
                            "remark" to auditContent,
                        )
                    )
                )
            )

            withContext(Dispatchers.Main) {
                LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                SToast.showToast(context, R.string.save_success)
            }
            jump.postValue(0)
        })
    }
}