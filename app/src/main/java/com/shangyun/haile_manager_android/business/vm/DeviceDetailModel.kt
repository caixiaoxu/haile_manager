package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.arguments.ItemShowParam
import com.shangyun.haile_manager_android.data.entities.DeviceAdvancedSettingEntity
import com.shangyun.haile_manager_android.data.entities.DeviceDetailEntity
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
class DeviceDetailModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    var goodsId: Int = -1

    val deviceDetail: MutableLiveData<DeviceDetailEntity> by lazy {
        MutableLiveData()
    }

    val isOpen: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val deviceAdvancedValues: MutableLiveData<MutableList<DeviceAdvancedSettingEntity>> by lazy {
        MutableLiveData()
    }

    val showFuncPrice: MutableLiveData<Boolean> = MutableLiveData(false)

    fun changeShowFuncPrice(view: View) {
        showFuncPrice.value =
            (!deviceDetail.value?.items.isNullOrEmpty()) && !(showFuncPrice.value ?: false)
    }

    val showAppointment: MutableLiveData<Boolean> = MutableLiveData(false)

    fun changeAppointment(view: View) {
        showAppointment.value =
            (null != deviceDetail.value) && !(showAppointment.value ?: false)
    }

    val showInfo: MutableLiveData<Boolean> = MutableLiveData(false)

    fun changeShowInfo(view: View) {
        showInfo.value =
            (null != deviceDetail.value) && !(showInfo.value ?: false)
    }

    /**
     * 获取设备的操作区域配置
     */
    fun getDeviceDetailFunOperate(
        hasReStart: LiveData<Boolean>,// 是否有重启权限
        hasStart: LiveData<Boolean>,// 是否有启动权限
        hasClean: LiveData<Boolean>,// 是否有桶自洁权限
        hasPayCode: LiveData<Boolean>,// 是否有付款码权限
        hasUpdate: LiveData<Boolean>,// 是否有修改权限
        hasAppointment: LiveData<Boolean> //是否有预约权限
    ): ArrayList<ItemShowParam> = arrayListOf(
        ItemShowParam(
            StringUtils.getString(R.string.restart),
            R.mipmap.icon_device_restart,
            hasReStart
        ) {
            //复位事件
            deviceOperate(0)
        },
        ItemShowParam(StringUtils.getString(R.string.start), R.mipmap.icon_device_start, hasStart) {
            //启动事件
        },
        ItemShowParam(
            StringUtils.getString(R.string.self_clean),
            R.mipmap.icon_device_self_clean,
            hasClean
        ) {
            //桶自洁事件
            deviceOperate(1)
        },
        ItemShowParam(
            StringUtils.getString(R.string.change_model),
            R.mipmap.icon_device_change_model,
            MutableLiveData(true)
        ) {
            // 更换模块事件
        },
        ItemShowParam(
            StringUtils.getString(R.string.change_pay_code),
            R.mipmap.icon_device_change_pay_code,
            hasPayCode
        ) {
            //付款码更换事件
        },
        ItemShowParam(
            StringUtils.getString(R.string.create_pay_code),
            R.mipmap.icon_device_create_pay_code,
            hasPayCode
        ) {
            //生成付款码事件
        },
        ItemShowParam(
            StringUtils.getString(R.string.update_func_price),
            R.mipmap.icon_device_update,
            hasUpdate
        ) {
            //修改功能价格事件
            jump.postValue(1)
        },
        ItemShowParam(
            StringUtils.getString(R.string.update_device_name),
            R.mipmap.icon_device_update,
            hasUpdate
        ) {
            //修改设备名称事件
        },
        ItemShowParam(
            StringUtils.getString(R.string.device_transfer),
            R.mipmap.icon_device_device_transfer,
            MutableLiveData(true)
        ) {
            //设备转移事件
        },
        ItemShowParam(
            StringUtils.getString(R.string.device_appointment_setting),
            R.mipmap.icon_device_device_appointment_setting,
            hasAppointment
        ) {
            //预约设置事件
        },
    )

    fun requestData() {
        if (-1 == goodsId) {
            return
        }

        launch({
            val detail = ApiRepository.dealApiResult(mDeviceRepo.deviceDetail(goodsId))
            detail?.let {
                deviceDetail.postValue(detail)
                isOpen.postValue(1 == detail.soldState)
            }

            val list = ApiRepository.dealApiResult(mDeviceRepo.deviceAdvancedValues(goodsId))
            list?.let {
                deviceAdvancedValues.postValue(it)
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }


    /**
     * 切换设备是否上架
     */
    fun switchDevice(isCheck: Boolean) {
        if (-1 == goodsId) {
            return
        }

        launch({
            val soldState = if (isCheck) 1 else 2
            ApiRepository.dealApiResult(
                mDeviceRepo.deviceUpdate(
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
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }

    /**
     * 设备删除
     */
    fun deviceDelete(view: View) {
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
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }

    /**
     * 设备操作
     */
    fun deviceOperate(type: Int) {
        if (-1 == goodsId) {
            return
        }

        launch({
            when (type) {
                0 -> ApiRepository.dealApiResult(
                    mDeviceRepo.deviceDelete(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "goodsId" to goodsId,
                            )
                        )
                    )
                )
                1-> ApiRepository.dealApiResult(
                    mDeviceRepo.deviceClean(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "goodsId" to goodsId,
                            )
                        )
                    )
                )
            }
            jump.postValue(0)
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }

}