package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.DeviceCategory
import com.yunshang.haile_manager_android.data.arguments.DeviceConfigSelectParams
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
class DeviceStartViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    var imei: String? = null
    var categoryCode: String? = null

    var items: List<DeviceConfigSelectParams> = arrayListOf()

    val selectItem: MutableLiveData<DeviceConfigSelectParams> by lazy {
        MutableLiveData()
    }

    val selectTime: MutableLiveData<Int> by lazy { MutableLiveData() }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(selectItem) {
            value = checkSubmit()
        }
        addSource(selectTime) {
            value = checkSubmit()
        }
    }

    private fun checkSubmit(): Boolean =
        (null != selectItem.value && null != selectTime.value)

    fun submit(view: View) {
        if (imei.isNullOrEmpty()) {
            return
        }

        if (categoryCode.isNullOrEmpty()) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceStart(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "itemId" to selectItem.value!!.id,
                            "time" to selectTime.value!!,
                            "imei" to imei!!,
                            "categoryCode" to categoryCode!!,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(view.context, "启动成功")
            }
        })
    }
}