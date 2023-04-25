package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.arguments.DeviceCreateItem
import com.shangyun.haile_manager_android.data.arguments.DeviceCreateParam
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.model.ApiRepository
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
class DeviceCreateAndUpdateViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    // 设备 创建/修改 数据
    val createAndUpdateEntity: MutableLiveData<DeviceCreateParam> =
        MutableLiveData(DeviceCreateParam())

    // 付款码
    val payCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // IMEI
    val imeiCode: MutableLiveData<String> = MutableLiveData()

    // 设备型号
    val createDeviceModelName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否是脉冲
    var communicationType: Int = -1

    // 所属门店
    val createDeviceShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    private val createDeviceShop1: LiveData<Boolean> = createDeviceShop.map {
        createAndUpdateEntity.value?.shopId = it.id
        false
    }

    // 功能配置
    val createDeviceFunConfigure: MutableLiveData<List<DeviceCreateItem>> by lazy {
        MutableLiveData()
    }

    /**
     * 根据imei请求型号
     */
    fun requestModelOfImei(imei: String) {
        launch({
            val deviceType = ApiRepository.dealApiResult(mRepo.deviceTypeOfImei(imei))
            deviceType?.let { type ->
                createAndUpdateEntity.value?.spuId = type.spu.id
                createAndUpdateEntity.value?.code = type.category.code
                createAndUpdateEntity.value?.communicationType = type.spu.communicationType
                createDeviceModelName.postValue(type.spu.name + type.spu.feature)
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
            jump.postValue(2)
        }, {
            Timber.d("请求结束")
        })
    }

    fun submit(view: View) {

    }
}