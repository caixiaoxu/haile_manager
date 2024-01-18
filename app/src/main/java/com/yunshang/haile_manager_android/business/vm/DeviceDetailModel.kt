package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.ItemShowParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.DeviceAdvancedSettingEntity
import com.yunshang.haile_manager_android.data.entities.DeviceDetailEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
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
class DeviceDetailModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    var goodsId: Int = -1

    val deviceDetail: MutableLiveData<DeviceDetailEntity> by lazy {
        MutableLiveData()
    }

    val hasSinglePulseQuantity: LiveData<Boolean> = deviceDetail.map {
        checkSinglePulseQuantity(it)
    }

    fun checkSinglePulseQuantity(detail: DeviceDetailEntity?): Boolean =
        !DeviceCategory.isDispenser(detail?.categoryCode) && 1 == detail?.items?.firstOrNull()?.extAttrDto?.items?.firstOrNull()?.priceCalculateMode

    val isOpen: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val name: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val code: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val imei: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val deviceAdvancedValues: MutableLiveData<MutableList<DeviceAdvancedSettingEntity>> by lazy {
        MutableLiveData()
    }

    val showFuncPrice: MutableLiveData<Boolean> = MutableLiveData(false)

    val showTemperature: MutableLiveData<Boolean> = MutableLiveData(true)

    val showErrorOrder: MutableLiveData<Boolean> = MutableLiveData(true)

    val showLiquid: MutableLiveData<Boolean> = MutableLiveData(true)

    val showParamsSetting: MutableLiveData<Boolean> = MutableLiveData(true)

    fun changeShowErrorOrder(view: View) {
        showErrorOrder.value = !(showErrorOrder.value ?: false)
    }

    fun changeShowShowParamsSetting(view: View) {
        showParamsSetting.value = !(showParamsSetting.value ?: false)
    }

    fun changeShowFuncPrice(view: View) {
        showFuncPrice.value =
            (!deviceDetail.value?.items.isNullOrEmpty()) && !(showFuncPrice.value ?: false)
    }

    fun changeShowLiquid(view: View) {
        showLiquid.value =
            (!deviceDetail.value?.items.isNullOrEmpty()) && !(showLiquid.value ?: false)
    }

    fun changeShowTemperature(view: View) {
        showTemperature.value =
            (!deviceDetail.value?.items.isNullOrEmpty()) && !(showTemperature.value ?: false)
    }

    val showAppointment: MutableLiveData<Boolean> = MutableLiveData(false)

    fun changeAppointment(view: View) {
        showAppointment.value =
            (null != deviceDetail.value) && !(showAppointment.value ?: false)
    }

    val showInfo: MutableLiveData<Boolean> = MutableLiveData(false)

    val showAssociation: MutableLiveData<Boolean> = MutableLiveData(false)

    fun changeShowInfo(view: View) {
        showInfo.value =
            (null != deviceDetail.value) && !(showInfo.value
                ?: false)
    }

    fun changeShowAssociation(view: View) {
        showAssociation.value =
            (null != deviceDetail.value) && (null != deviceDetail.value?.relatedGoodsDetailVo) && !(showAssociation.value
                ?: false)
    }

    /**
     * 获取设备的操作区域配置
     */
    val deviceDetailFunOperate: ArrayList<ItemShowParam> = arrayListOf(
        ItemShowParam(
            R.string.restart,
            R.mipmap.icon_device_restart,
            MutableLiveData(UserPermissionUtils.hasDeviceResetPermission())
        ) {
            //复位事件
            jump.postValue(8)
        },
        ItemShowParam(
            R.string.start,
            R.mipmap.icon_device_start,
            MutableLiveData(UserPermissionUtils.hasDeviceStartPermission())
        ) {
            //启动事件
            jump.postValue(2)
        },
        ItemShowParam(
            R.string.devices_self_clean,
            R.mipmap.icon_device_device_selfclean,
            MutableLiveData(UserPermissionUtils.hasDeviceAppointmentPermission())
        ) {
            //排空设置事件
            jump.postValue(12)
        },
        ItemShowParam(
            R.string.self_clean,
            R.mipmap.icon_device_self_clean,
            MutableLiveData(UserPermissionUtils.hasDeviceCleanPermission())
        ) {
            //筒自洁事件
            deviceOperate(1)
        },
        ItemShowParam(
            R.string.unlock,
            R.mipmap.icon_device_unlock,
            MutableLiveData(UserPermissionUtils.hasDeviceStartPermission())
        ) {
            //启动事件
            jump.postValue(9)
        },
        ItemShowParam(
            R.string.unlock1,
            R.mipmap.icon_device_unlock,
            MutableLiveData(UserPermissionUtils.hasDeviceStartPermission())
        ) {
            //启动事件
            jump.postValue(9)
        },
        ItemShowParam(
            R.string.change_model,
            R.mipmap.icon_device_change_model,
            MutableLiveData(UserPermissionUtils.hasDeviceUpdateModulePermission())
        ) {
            // 更换模块事件
            jump.postValue(3)
        },
        ItemShowParam(
            R.string.change_pay_code,
            R.mipmap.icon_device_change_pay_code,
            MutableLiveData(UserPermissionUtils.hasDeviceQrcodePermission())
        ) {
            //付款码更换事件
            jump.postValue(4)
        },
        ItemShowParam(
            R.string.create_pay_code,
            R.mipmap.icon_device_create_pay_code,
            MutableLiveData(UserPermissionUtils.hasDeviceQrcodePermission())
        ) {
            //生成付款码事件
            jump.postValue(6)
        },
        ItemShowParam(
            R.string.device_transfer,
            R.mipmap.icon_device_device_transfer,
            MutableLiveData(UserPermissionUtils.hasDeviceExchangePermission())
        ) {
            //设备转移事件
            jump.postValue(15)
        },
        ItemShowParam(
            R.string.update_func_price,
            R.mipmap.icon_device_update,
            MutableLiveData(UserPermissionUtils.hasDeviceUpdatePermission())
        ) {
            //修改功能价格事件
            jump.postValue(1)
        },
        ItemShowParam(
            R.string.update_device_name,
            R.mipmap.icon_device_update,
            MutableLiveData(UserPermissionUtils.hasDeviceUpdatePermission())
        ) {
            //修改设备名称事件
            jump.postValue(5)
        },
        ItemShowParam(
            R.string.update_params_setting,
            R.mipmap.icon_device_update,
            MutableLiveData(UserPermissionUtils.hasDeviceUpdatePermission())
        ) {
            //修改设备名称事件
            jump.postValue(13)
        },
        ItemShowParam(
            R.string.device_appointment_setting,
            R.mipmap.icon_device_device_appointment_setting,
            MutableLiveData(UserPermissionUtils.hasDeviceAppointmentPermission())
        ) {
            //预约设置事件
            jump.postValue(7)
        },

        ItemShowParam(
            R.string.device_voice,
            R.mipmap.icon_device_device_voice,
            MutableLiveData(UserPermissionUtils.hasDeviceAppointmentPermission())
        ) {
            //语音设置事件
            jump.postValue(10)
        },
        ItemShowParam(
            R.string.device_drain,
            R.mipmap.icon_device_device_drain,
            MutableLiveData(UserPermissionUtils.hasDeviceAppointmentPermission())
        ) {
            //排空设置事件
            jump.postValue(11)
        },
        ItemShowParam(
            R.string.update_floor,
            R.mipmap.icon_device_update,
            MutableLiveData(UserPermissionUtils.hasDeviceUpdatePermission())
        ) {
            //排空设置事件
            jump.postValue(14)
        },
        ItemShowParam(
            R.string.hot_clean_self,
            R.mipmap.icon_device_hot_clean_self,
            MutableLiveData(UserPermissionUtils.hasHotCleanSelfPermission())
        ) {
            //高温筒自洁事件
            jump.postValue(16)
        },
    )

    fun requestData(type: Int = 0) {
        if (-1 == goodsId) {
            return
        }

        launch({
            if (0 == type || 1 == type) {
                requestDeviceDetails()
            }

            if (0 == type || 2 == type) {
                val list = ApiRepository.dealApiResult(mDeviceRepo.deviceAdvancedValues(goodsId))
                list?.let {
                    deviceAdvancedValues.postValue(it)
                }
            }
        })
    }

    private suspend fun requestDeviceDetails() {
        val detail = ApiRepository.dealApiResult(mDeviceRepo.deviceDetail(goodsId))
        detail?.let {
            deviceDetail.postValue(detail)
            isOpen.postValue(1 == detail.soldState)
            name.postValue(detail.name)
            code.postValue(detail.code)
            imei.postValue(detail.imei)
        }
    }


    /**
     * 切换设备是否上架
     */
    fun switchDevice(isCheck: Boolean) {
        if (-1 == goodsId) {
            return
        }

        val soldState = if (isCheck) 1 else 2
        // 避免重复提交
        if (deviceDetail.value?.soldState == soldState) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceUpdateV2(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to goodsId,
                            "soldState" to soldState,
                        )
                    )
                )
            )
            deviceDetail.value?.soldState = soldState
            isOpen.postValue(isCheck)
        })
    }

    /**
     * 设备删除
     */
    fun deviceDelete() {
        if (-1 == goodsId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceDelete(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsId" to goodsId,
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.DEVICE_LIST_ITEM_DELETE_STATUS, goodsId)
            jump.postValue(0)
        })
    }

    /**
     * 设备操作
     */
    fun deviceOperate(type: Int, itemId: Int? = null) {
        if (-1 == goodsId) {
            return
        }

        launch({
            when (type) {
                0 -> {
                    ApiRepository.dealApiResult(
                        mDeviceRepo.deviceReset(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "goodsId" to goodsId,
                                ).apply {
                                    itemId?.let {
                                        put("itemId", it)
                                    }
                                }
                            )
                        )
                    )
                    withContext(Dispatchers.Main) {
                        SToast.showToast(msg = "复位成功")
                    }
                }

                1 -> {
                    ApiRepository.dealApiResult(
                        mDeviceRepo.deviceClean(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "goodsId" to goodsId,
                                )
                            )
                        )
                    )
                    withContext(Dispatchers.Main) {
                        SToast.showToast(msg = "开始筒自洁")
                    }
                }
            }
            requestDeviceDetails()
            LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
        })
    }

    /**
     * 启动饮水设备
     */
    fun startDrinkingDevice(
        itemId: Int?,
        imei: String,
        categoryCode: String,
        callBack: () -> Unit
    ) {
        itemId?.let {
            launch({
                ApiRepository.dealApiResult(
                    mDeviceRepo.deviceStart(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "itemId" to itemId,
                                "imei" to imei,
                                "categoryCode" to categoryCode,
                            )
                        )
                    )
                )
                LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
                withContext(Dispatchers.Main) {
                    callBack()
                }
            })
        }
    }

    fun openOrCloseAppointment(isOpen: Boolean, callBack: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.openOrCloseDeviceAppointment(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsId" to goodsId,
                            "value" to isOpen
                        )
                    )
                )
            )
            requestDeviceDetails()
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }

    /**
     * 设备设置
     */
    fun deviceSetting(settingType: Int, imei: String) {
        if (imei.isNullOrEmpty()) {
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.devicePassSetting(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "imei" to imei,
                            "settingType" to settingType,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(msg = "操作成功")
            }
        })

    }

    /**
     * 投放器重置100%
     */
    fun deviceActivate(liquidType: Int, activationCode: String) {
        if (imei.value.isNullOrEmpty()) {
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceActivate(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "liquidType" to liquidType,
                            "activationCode" to activationCode,
                            "goodsId" to goodsId,
                        )
                    )
                )
            )
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceOpenCapFinish(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "liquidType" to liquidType,
                            "imei" to imei.value!!,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(msg = "更换完成，已重置100%")
                requestData()
            }

        })
    }

    /**
     * 投放器开盖
     */
    fun deviceOpenCap(imei: String) {
        if (imei.isNullOrEmpty()) {
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceOpenCap(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "imei" to imei,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(msg = "操作成功")
            }
        })
    }

    fun preTransferDevice(callBack: (type: Int?) -> Unit) {
        if (!goodsId.hasVal()) return
        launch({
            val result = ApiRepository.dealApiResult(
                mDeviceRepo.preTransferDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsIds" to listOf(goodsId),
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack(result)
            }
        })
    }

    fun transferDevice(context: Context, positionId: Int?) {
        if (null == positionId || !goodsId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.transferDevice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "goodsIds" to listOf(goodsId),
                            "positionId" to positionId,
                        )
                    )
                )
            )

            LiveDataBus.post(BusEvents.DEVICE_LIST_STATUS, true)
            LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)

            requestDeviceDetails()
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.operate_success)
            }
        })
    }
}