package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
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
class DeviceRepairsReplyViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    val repairsDetails: MutableLiveData<DeviceRepairsEntity> by lazy {
        MutableLiveData()
    }

    var id: Int? = null

    val replyContent = ReplyContent()

    fun requestData() {
        if (!id.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.requestDeviceRepairsDetails(id!!)
            )?.let {
                repairsDetails.postValue(it)
            }
        })
    }

    fun replyFaultRepairs(v: View) {
        if (!id.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.replyDeviceRepairs(
                    ApiRepository.createRequestBody(
                        hashMapOf(
//                            "deviceIds" to listOf<>(repairsDetails.value.deviceId),
                            "deviceFixLogIds" to listOf(id),
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

    data class ReplyContent(
        var _replyContent: String? = null,
        var _replyContentNum: String = "0/300",
    ) : BaseObservable() {

        @get:Bindable
        var replyContent: String?
            get() = _replyContent
            set(value) {
                _replyContent = value
                notifyPropertyChanged(BR.replyContent)
                replyContentNum = "${_replyContent?.length ?: 0}/300"
            }

        @get:Bindable
        var replyContentNum: String
            get() = _replyContentNum
            set(value) {
                _replyContentNum = value
                notifyPropertyChanged(BR.replyContentNum)
            }
    }
}