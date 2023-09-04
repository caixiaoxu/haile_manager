package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.gson.internal.LinkedTreeMap
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
 * Date: 2023/9/4 15:49
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceOtherParamsUpdateViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    var updateParams: HashMap<String, Any?>? = null

    val singlePulseQuantityVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun skuList() = (updateParams?.get("items") as? List<*>)

    fun attrList(attrMap: Map<*, *>?) =
        ((attrMap?.get("extAttrDto") as? Map<*, *>)?.get(
            "items"
        ) as? List<*>)

    fun save(v: View) {
        if (null == updateParams) {
            return
        }

        val quantity = try {
            singlePulseQuantityVal.value?.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
            SToast.showToast(v.context, "请输入正常值")
            return
        }

        launch({
            quantity?.let {
                skuList()?.forEach { sku ->
                    attrList(sku as? Map<*, *>)?.forEach { attr ->
                        (attr as? LinkedTreeMap<String, Any>)?.put("pulseVolumeFactor", quantity)
                    }
                }
            }

            ApiRepository.dealApiResult(
                mDeviceRepo.deviceUpdateV2(
                    ApiRepository.createRequestBody(updateParams!!)
                )
            )
            withContext(Dispatchers.Main) {
                LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
                LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                SToast.showToast(v.context, R.string.update_success)
            }
            jump.postValue(0)
        })
    }
}