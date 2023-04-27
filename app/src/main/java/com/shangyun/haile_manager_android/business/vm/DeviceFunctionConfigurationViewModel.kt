package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.arguments.DeviceCategory
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

        // 参数判断
        configurationList.value?.let { list ->
            list.forEachIndexed { index, config ->
                val no = index + 1
                if (DeviceCategory.isDryer(categoryCode)) {
                    if (config.name.isEmpty()) {
                        SToast.showToast(msg = "请先输入功能配置${no}的名称")
                        return
                    }
                    if (config.feature.isEmpty()) {
                        SToast.showToast(msg = "请先输入功能配置${no}的描述信息")
                        return
                    }
                    if (config.extAttrValue.isNullOrEmpty() || config.extAttr.isEmpty()) {
                        SToast.showToast(msg = "请先选择功能配置${no}的烘干时间")
                        return
                    }

                    config.extAttrValue?.forEach {
                        if (it.priceValue.isNullOrEmpty() || -1.0 == it.price) {
                            SToast.showToast(msg = "请先输入功能配置${no}的烘干时间${it.minutes}分钟的价格")
                            return
                        }
                        if (DeviceCategory.isPulseDevice(communicationType)) {
                            if (it.pulseValue.isNullOrEmpty() || -1 == it.pulse) {
                                SToast.showToast(msg = "请先输入功能配置${no}的烘干时间${it.minutes}分钟的脉冲数")
                                return
                            }
                        }
                    }
                } else {
                    if (config.name.isEmpty()) {
                        SToast.showToast(msg = "请先输入功能配置${no}的名称")
                        return
                    }
                    if (config.unitValue.isNullOrEmpty() || -1 == config.unit) {
                        SToast.showToast(msg = "请先输入功能配置${no}的洗涤时间")
                        return
                    }
                    if (config.priceValue.isNullOrEmpty() || -1.0 == config.price) {
                        SToast.showToast(msg = "请先输入功能配置${no}的洗涤费用")
                        return
                    }

                    if (DeviceCategory.isPulseDevice(communicationType)) {
                        if (config.pulseValue.isNullOrEmpty() || -1 == config.pulse) {
                            SToast.showToast(msg = "请先输入功能配置${no}的脉冲数")
                            return
                        }
                    }

                    if (config.feature.isEmpty()) {
                        SToast.showToast(msg = "请先输入功能配置${no}的描述信息")
                        return
                    }
                }
            }
        }

        resultData.postValue(
            configurationList.value?.map {
                it.getRequestParams()
            }
        )
    }
}