package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.model.ApiRepository
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
class DeviceMultiChangeViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    var goodId = -1

    companion object {
        const val Type = "Type"

        const val typeChangeModel = 0
        const val typeChangePayCode = 1
        const val typeChangeName = 2
    }

    val type: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val titles = arrayOf(
        StringUtils.getString(R.string.change_model),
        StringUtils.getString(R.string.change_pay_code),
        StringUtils.getString(R.string.update_device_name),
    )

    val multiChangeTitles = arrayOf(
        StringUtils.getString(R.string.update_imei_title),
        StringUtils.getString(R.string.update_pay_code_title),
        StringUtils.getString(R.string.update_name_title),
    )

    val multiChangeTitle: LiveData<String> = type.map { multiChangeTitles[it] }

    val multiChangeHints = arrayOf(
        StringUtils.getString(R.string.update_imei_hint),
        StringUtils.getString(R.string.update_pay_code_hint),
        StringUtils.getString(R.string.update_name_hint),
    )

    val multiChangeHint: LiveData<String> = type.map { multiChangeHints[it] }

    val multiChangeDescs = arrayOf(
        StringUtils.getString(R.string.update_imei_desc),
        StringUtils.getString(R.string.update_pay_code_desc),
        StringUtils.getString(R.string.update_name_desc),
    )

    val multiChangeDesc: LiveData<String> = type.map { multiChangeDescs[it] }

    val content: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(content) {
            value = !it.isNullOrEmpty()
        }
    }

    fun submit(view: View) {
        if (-1 == goodId) {
            return
        }

        launch({
            val params = HashMap<String, Any>()
            params["id"] = goodId
            when (type.value) {
                typeChangeModel -> params["imei"] = content.value!!
                typeChangePayCode -> params["code"] = content.value!!
                typeChangeName -> params["name"] = content.value!!
            }

            ApiRepository.dealApiResult(
                mDeviceRepo.deviceUpdate(
                    ApiRepository.createRequestBody(params)
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(view.context, R.string.update_success)
            }
            jump.postValue(0)
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }
}