package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.arguments.DeviceCategory
import com.shangyun.haile_manager_android.data.arguments.DeviceCreateParam
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.SkuFuncConfigurationParam
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

    var id: Int = -1

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

    val isSelectedModel: MutableLiveData<Boolean> = MutableLiveData(false)

    var deviceCategoryCode: String? = ""

    // 是否是脉冲
    var deviceCommunicationType: Int = -1

    // 设备名称
    val deviceName: MutableLiveData<String> = MutableLiveData()

    // 所属门店
    val createDeviceShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 功能配置
    val createDeviceFunConfigure: MutableLiveData<List<SkuFuncConfigurationParam>> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(payCode) {
            value = checkSubmit()
        }
        addSource(imeiCode) {
            value = checkSubmit()
        }
        addSource(createDeviceModelName) {
            value = checkSubmit()
        }
        addSource(deviceName) {
            value = checkSubmit()
        }
        addSource(createDeviceShop) {
            value = checkSubmit()
        }
        addSource(createDeviceFunConfigure) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean = (!createAndUpdateEntity.value?.code.isNullOrEmpty()
            && !createAndUpdateEntity.value?.imei.isNullOrEmpty()
            && (-1 != createAndUpdateEntity.value?.spuId)
            && (-1 != createAndUpdateEntity.value?.shopCategoryId)
            && !createAndUpdateEntity.value?.name.isNullOrEmpty()
            && (-1 != createAndUpdateEntity.value?.shopId)
            && null != createDeviceFunConfigure.value)

    /**
     * 切换设备型号
     */
    fun changeDeviceModel(
        spuId: Int,
        spuName: String?,
        categoryId: Int,
        categoryCode: String?,
        communicationType: Int
    ) {
        createAndUpdateEntity.value?.spuId = spuId
        createDeviceModelName.value = spuName
        createAndUpdateEntity.value?.shopCategoryId = categoryId
        deviceCategoryCode = categoryCode
        deviceCommunicationType = communicationType
        isSelectedModel.value = true
        createDeviceFunConfigure.value = null
    }

    /**
     * 根据imei请求型号
     */
    fun requestModelOfImei(imei: String) {
        launch({
            val deviceType = ApiRepository.dealApiResult(mRepo.deviceTypeOfImei(imei))
            deviceType?.let { type ->
                changeDeviceModel(
                    type.spu.id,
                    type.spu.name + type.spu.feature,
                    type.category.id,
                    type.category.code,
                    type.spu.communicationType
                )
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
            jump.postValue(2)
        }, {
            Timber.d("请求结束")
        })
    }

    /**
     * 创建设备
     */
    fun submit(view: View) {
        launch({
            ApiRepository.dealApiResult(
                mRepo.deviceCreate(
                    ApiRepository.createRequestBody(
                        createAndUpdateEntity.value?.toDeviceJson(id) ?: ""
                    )
                )
            )
            LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
            jump.postValue(0)
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        })
    }
}