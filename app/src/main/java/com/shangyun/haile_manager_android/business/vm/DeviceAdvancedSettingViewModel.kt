package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.entities.DeviceAdvancedEntity
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
class DeviceAdvancedSettingViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    var goodId: Int = -1
    var functionId: Int = -1
    val attrs: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val attrList: LiveData<MutableList<DeviceAdvancedEntity>?> = attrs.map {
        GsonUtils.json2List(it, DeviceAdvancedEntity::class.java)
    }

    fun submit(view: View) {
        if (-1 == goodId || -1 == functionId || attrList.value.isNullOrEmpty()) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceAdvancedSetting(
                    hashMapOf(
                        "goodsId" to goodId,
                        "settingStr" to GsonUtils.any2Json(
                            arrayListOf(
                                hashMapOf(
                                    "functionId" to functionId,
                                    "values" to attrList.value!!.map { if (it.inputValue.isNullOrEmpty()) it.input else it.inputValue },
                                )
                            )
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(view.context, "设置成功")
            }
            jump.postValue(0)
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        })
    }
}