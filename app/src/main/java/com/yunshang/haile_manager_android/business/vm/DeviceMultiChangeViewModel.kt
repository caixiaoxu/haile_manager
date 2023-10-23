package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.IntentParams
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
class DeviceMultiChangeViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    var updateParams: HashMap<String, Any?>? = null

    val type: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val titles = arrayOf(
        StringUtils.getString(R.string.change_model),
        StringUtils.getString(R.string.change_pay_code),
        StringUtils.getString(R.string.update_device_name),
        StringUtils.getString(R.string.update_floor),
    )

    val multiChangeTitles = arrayOf(
        StringUtils.getString(R.string.update_imei_title),
        StringUtils.getString(R.string.update_pay_code_title),
        StringUtils.getString(R.string.update_name_title),
        StringUtils.getString(R.string.update_device_floor),
    )

    val multiChangeTitle: LiveData<String> = type.map { multiChangeTitles[it] }

    val multiChangeHints = arrayOf(
        StringUtils.getString(R.string.update_imei_hint),
        StringUtils.getString(R.string.update_pay_code_hint),
        StringUtils.getString(R.string.update_name_hint),
        StringUtils.getString(R.string.update_floor_hint),
    )

    val multiChangeHint: LiveData<String> = type.map { multiChangeHints[it] }

    val multiChangeDescs = arrayOf(
        StringUtils.getString(R.string.update_imei_desc),
        StringUtils.getString(R.string.update_pay_code_desc),
        StringUtils.getString(R.string.update_name_desc),
        StringUtils.getString(R.string.update_floor_desc),
    )

    val multiChangeDesc: LiveData<String> = type.map { multiChangeDescs[it] }

    val content: MutableLiveData<String?> by lazy {
        MutableLiveData()
    }

    var originCode: String? = null

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(content) {
            value = !it.isNullOrEmpty()
        }
    }

    fun submit(view: View) {
        if (null == updateParams) {
            return
        }

        launch({
            when (type.value) {
                IntentParams.DeviceParamsUpdateParams.typeChangeModel -> updateParams!!["imei"] = content.value!!
                IntentParams.DeviceParamsUpdateParams.typeChangePayCode -> {
                    updateParams!!["code"] = content.value!!
                    updateParams!!["codeStr"] = originCode
                }
                IntentParams.DeviceParamsUpdateParams.typeChangeName -> updateParams!!["name"] = content.value!!
                IntentParams.DeviceParamsUpdateParams.typeChangeFloor -> updateParams!!["floorCode"] = content.value!!
            }

            ApiRepository.dealApiResult(
                mDeviceRepo.deviceUpdateV2(
                    ApiRepository.createRequestBody(updateParams!!)
                )
            )
            withContext(Dispatchers.Main) {
                LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
                LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                SToast.showToast(view.context, R.string.update_success)
            }
            jump.postValue(0)
        })
    }
}