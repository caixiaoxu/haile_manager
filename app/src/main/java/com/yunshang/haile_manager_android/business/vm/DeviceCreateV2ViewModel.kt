package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.data.model.ApiRepository

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

    // 付款码
    val payCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // IMEI
    val imeiCode: MutableLiveData<String> = MutableLiveData()

    // 付款码源链接
    var codeStr: String? = null

    // 所属门店
    val createDeviceShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    var spuId: Int = -1

    // 设备类型
    val categoryName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 设备型号
    val modelName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    var categoryId: Int = -1
    val categoryCode: MutableLiveData<String> = MutableLiveData()
    val hasCategoryCode: LiveData<Boolean> = categoryCode.map {
        !it.isNullOrEmpty()
    }
    val isDispenser: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isDispenser(it)
    }
    val isDrinking: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isDrinking(it)
    }

    // 脉冲
    var deviceCommunicationType: Int = -1

    // 是否是二码合一
    var deviceIgnorePayCodeFlag: Boolean = false

    //设备名
    val deviceName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 脉冲值
    val communicationVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 投放器绑定的洗衣机imei
    val washImeiCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 功能配置
    val createDeviceFunConfigure: MutableLiveData<List<SkuFuncConfigurationParam>> =
        MutableLiveData()

    val isFunConfigure: LiveData<Boolean> = createDeviceFunConfigure.map {
        !it.isNullOrEmpty()
    }

    /**
     * 根据imei请求型号
     */
    fun requestModelOfImei(imei: String) {
        launch({
            ApiRepository.dealApiResult(mRepo.deviceTypeOfImei(imei))?.let {
                initDeviceCategoryAndModel(
                    it.spu.id,
                    it.category.name,
                    it.spu.name + it.spu.feature,
                    it.category.id,
                    it.category.code,
                    it.spu.communicationType,
                    it.spu.getIgnorePayCodeFlag()
                )
            }
        })
    }

    /**
     * 初始化设备类型模式选择
     */
    fun initDeviceCategoryAndModel(
        sId: Int,
        cName: String?,
        feature: String?,
        cId: Int,
        code: String?,
        communicationType: Int,
        ignorePayCodeFlag: Boolean
    ) {
        spuId = sId
        categoryName.postValue(cName ?: "")
        modelName.postValue(feature ?: "")
        categoryId = cId
        categoryCode.postValue(code ?: "")
        deviceCommunicationType = communicationType
        deviceIgnorePayCodeFlag = ignorePayCodeFlag
    }

    fun save(view: View) {
    }
}