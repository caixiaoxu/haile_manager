package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.entities.SkuEntity
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
class DeviceFunctionConfigurationViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // spuid
    var spuId: Int = -1

    // 设备类型
    var categoryCode: String? = null

    // 10-串口 20-脉冲
    var communicationType: Int = -1

    // 旧的方法配置
    var oldConfigurationList: List<SkuFuncConfigurationParam>? = null

    // 原方法配置
    val configurationList: MutableLiveData<MutableList<SkuEntity>> by lazy {
        MutableLiveData()
    }

    // 返回参数
    val resultData: MutableLiveData<List<SkuFuncConfigurationParam>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == spuId) {
            return
        }

        launch({
            val list = ApiRepository.dealApiResult(mDeviceRepo.sku(spuId))
            list?.let {
                if (!oldConfigurationList.isNullOrEmpty()) {
                    it.forEach { sku ->
                        sku.mergeOld(oldConfigurationList!!.find { param -> param.skuId == sku.id })
                    }
                }
                configurationList.postValue(it)
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }

    fun save(view: View) {
        resultData.postValue(
            configurationList.value?.map {
                it.getRequestParams()
            }
        )
    }
}