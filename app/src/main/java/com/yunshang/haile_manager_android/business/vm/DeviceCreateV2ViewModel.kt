package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep1Fragment
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep2Fragment
import com.yunshang.haile_manager_android.ui.fragment.device.DeviceCreateStep3Fragment

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/24 10:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceCreateV2ViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    // 步骤
    val step: MutableLiveData<Int> = MutableLiveData(0)

    // 步骤对应的fragment
    val deviceCreateStepFragments = listOf(
        DeviceCreateStep1Fragment(),
        DeviceCreateStep2Fragment(),
        DeviceCreateStep3Fragment(),
    )

    // 付款码
    val payCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // IMEI
    val imeiCode: MutableLiveData<String> = MutableLiveData()

    // 付款码源链接
    val codeStr: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    var spuId: Int = -1
    val categoryName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    var categoryId: Int = -1
    var categoryCode: String = ""
    var deviceCommunicationType: Int = -1

    // 功能配置
    val createDeviceFunConfigure: MutableLiveData<List<SkuFuncConfigurationParam>> by lazy {
        MutableLiveData()
    }

    /**
     * 根据imei请求型号
     */
    fun requestModelOfImei(imei: String) {
        launch({
            ApiRepository.dealApiResult(mRepo.deviceTypeOfImei(imei))?.let {
                spuId = it.spu.id
                categoryName.postValue(it.spu.name + it.spu.feature)
                categoryId = it.category.id
                categoryCode = it.category.code
                deviceCommunicationType = it.spu.communicationType
            }
        })
    }
}