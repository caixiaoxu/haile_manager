package com.yunshang.haile_manager_android.business.vm

import android.view.View
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/23 09:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceRepairsBatchReplyViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    var fixIds: IntArray? = null

    var deviceIds: IntArray? = null

    val replyContent = DeviceRepairsReplyViewModel.ReplyContent()

    fun replyFaultRepairs(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.replyDeviceRepairs(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "deviceIds" to deviceIds,
                            "deviceFixLogIds" to fixIds,
                            "replyContent" to replyContent.replyContent,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.submit_success)
            }
            LiveDataBus.post(BusEvents.DEVICE_FAULT_REPAIRS_REPLY_STATUS, true)
            jump.postValue(0)
        })
    }
}