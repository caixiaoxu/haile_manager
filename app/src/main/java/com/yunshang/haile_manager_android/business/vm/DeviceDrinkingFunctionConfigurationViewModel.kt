package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.DrinkAttrConfigure
import com.yunshang.haile_manager_android.data.entities.ExtAttrDrinkBean
import com.yunshang.haile_manager_android.data.entities.SkuEntity
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
class DeviceDrinkingFunctionConfigurationViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // goodsId
    var goodsId: Int = -1

    // spuid
    var spuId: Int = -1

    // 设备类型
    var categoryCode: String? = null

    // 旧的方法配置
    var oldConfigurationList: List<SkuFuncConfigurationParam>? = null

    // 配置列表
    var configurationList: MutableList<SkuEntity>? = null

    // 饮水配置
    val drinkAttrConfigure: MutableLiveData<DrinkAttrConfigure> by lazy {
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
            ApiRepository.dealApiResult(mDeviceRepo.sku(spuId))?.let {
                it.forEach { sku ->
                    // 如果有旧数据，合并旧数据，如果没有，配置默认数据
                    if (!oldConfigurationList.isNullOrEmpty()) {
                        sku.mergeDrinkOld(oldConfigurationList!!.find { param -> param.skuId == sku.id })
                    } else {
                        sku.extAttrDrink = GsonUtils.json2JsonObject(sku.extAttr)
                    }
                }
                it.firstOrNull()?.let { first ->
                    first.extAttrDrink?.let { firstAttr ->
                        drinkAttrConfigure.postValue(
                            DrinkAttrConfigure(
                                MutableLiveData(firstAttr["priceCalculateMode"].asInt),
                                try {
                                    firstAttr["overTime"].asInt
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    10
                                },
                                try {
                                    firstAttr["pauseTime"].asInt
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    10
                                },
                                try {
                                    firstAttr["singlePulseQuantity"].asDouble
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    0.0001
                                },
                                it.map { sku ->
                                    DrinkAttrConfigure.DrinkAttrConfigureItem(
                                        sku.name,
                                        sku.price,
                                        sku.soldState
                                    )
                                }
                            )
                        )
                    }
                }
                configurationList = it
            }
        })
    }

    fun save(v: View) {
        if (null == drinkAttrConfigure.value) return
        if (drinkAttrConfigure.value?.overTime.isNullOrEmpty()) {
            SToast.showToast(msg = "请先输入过流时间")
            return
        }
        if (10 > drinkAttrConfigure.value!!._overTime || 600 < drinkAttrConfigure.value!!._overTime) {
            SToast.showToast(msg = "过流时间输入范围为10-600")
            return
        }
        if (drinkAttrConfigure.value?.pauseTime.isNullOrEmpty()) {
            SToast.showToast(msg = "请先输入暂停时间")
            return
        }
        if (10 > drinkAttrConfigure.value!!._pauseTime || 600 < drinkAttrConfigure.value!!._pauseTime) {
            SToast.showToast(msg = "暂停时间输入范围为10-600")
            return
        }
        if (1 == drinkAttrConfigure.value?.priceCalculateMode?.value
            && drinkAttrConfigure.value?.singlePulseQuantity.isNullOrEmpty()
        ) {
            SToast.showToast(msg = "请先输入单脉冲流量")
            return
        }
        if (1 == drinkAttrConfigure.value?.priceCalculateMode?.value
            && (0.001 > drinkAttrConfigure.value!!._singlePulseQuantity || 50.0 < drinkAttrConfigure.value!!._singlePulseQuantity)
        ) {
            SToast.showToast(msg = "单脉冲流量范围为0.001-50.0ml")
            return
        }
        if (drinkAttrConfigure.value?.items.isNullOrEmpty()) return
        if (drinkAttrConfigure.value?.items?.all { item -> 2 == item.soldState } != false) {
            SToast.showToast(msg = "请至少开启一个单价")
            return
        }
        drinkAttrConfigure.value?.items?.forEach { item ->
            if (item.priceValue.isEmpty()) {
                SToast.showToast(msg = "请输入${item.title}单价")
                return
            }
            if (0 > item.price || item.price > 10) {
                SToast.showToast(msg = "${item.title}单价的输入范围为0.00-10.00")
                return
            }
        }

        // 重新赋值
        val items = drinkAttrConfigure.value!!.items
        val itemCount = items.size
        configurationList?.forEachIndexed { index, sku ->
            if (itemCount > index) {
                sku.price = items[index].price
                sku.soldState = items[index].soldState
            }
            sku.extAttrDrink?.addProperty(
                "priceCalculateMode",
                drinkAttrConfigure.value!!.priceCalculateMode.value
            )
            sku.extAttrDrink?.addProperty(
                "overTime",
                drinkAttrConfigure.value!!._overTime.toString()
            )
            sku.extAttrDrink?.addProperty(
                "pauseTime",
                drinkAttrConfigure.value!!._pauseTime.toString()
            )
            sku.extAttrDrink?.addProperty(
                "singlePulseQuantity",
                drinkAttrConfigure.value!!._singlePulseQuantity.toString()
            )
        }
        val params = configurationList?.map {
            it.getDrinkRequestParams()
        } ?: arrayListOf()
        if (-1 == goodsId) {
            resultData.postValue(params)
        } else {
            launch({
                ApiRepository.dealApiResult(
                    mDeviceRepo.deviceUpdate(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "id" to goodsId,
                                "items" to params
                            )
                        )
                    )
                )
                withContext(Dispatchers.Main) {
                    SToast.showToast(msgResId = R.string.update_success)
                }
                LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                jump.postValue(0)
            })
        }
    }
}