package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.arguments.DeviceCreateItem
import com.shangyun.haile_manager_android.data.arguments.DeviceCreateParam
import com.shangyun.haile_manager_android.data.model.ApiRepository

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
class DeviceCreateAndUpdateViewModel: BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    // 设备 创建/修改 数据
    val createAndUpdateEntity: MutableLiveData<DeviceCreateParam> =
        MutableLiveData(DeviceCreateParam())

    // 设备型号
    val createDeviceModelName:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 所属门店
    val createDeviceShopName:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 功能配置
    val createDeviceFunConfigure:MutableLiveData<List<DeviceCreateItem>> by lazy {
        MutableLiveData()
    }
}