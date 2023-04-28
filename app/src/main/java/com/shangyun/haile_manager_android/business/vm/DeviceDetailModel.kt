package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.arguments.ItemShowParam
import com.shangyun.haile_manager_android.data.entities.DeviceAdvancedSettingEntity
import com.shangyun.haile_manager_android.data.entities.DeviceDetailEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.ArrayList

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

    val deviceAdvancedValues: MutableLiveData<MutableList<DeviceAdvancedSettingEntity>> by lazy {
        MutableLiveData()
    }

    val showFuncPrice: MutableLiveData<Boolean> = MutableLiveData(false)

    fun changeShowFuncPrice(view: View) {
        showFuncPrice.value =
            (!deviceDetail.value?.items.isNullOrEmpty()) && !(showFuncPrice.value ?: false)
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
        ),
        ItemShowParam(StringUtils.getString(R.string.start), R.mipmap.icon_device_start, hasStart),
        ItemShowParam(
            StringUtils.getString(R.string.self_clean),
            R.mipmap.icon_device_self_clean,
            hasClean
        ),
        ItemShowParam(
            StringUtils.getString(R.string.change_model),
            R.mipmap.icon_device_change_model,
            MutableLiveData(true)
        ),
        ItemShowParam(
            StringUtils.getString(R.string.change_pay_code),
            R.mipmap.icon_device_change_pay_code,
            hasPayCode
        ),
        ItemShowParam(
            StringUtils.getString(R.string.create_pay_code),
            R.mipmap.icon_device_create_pay_code,
            hasPayCode
        ),
        ItemShowParam(
            StringUtils.getString(R.string.update_func_price),
            R.mipmap.icon_device_update,
            hasUpdate
        ),
        ItemShowParam(
            StringUtils.getString(R.string.update_device_name),
            R.mipmap.icon_device_update,
            hasUpdate
        ),
        ItemShowParam(
            StringUtils.getString(R.string.device_transfer),
            R.mipmap.icon_device_device_transfer,
            MutableLiveData(true)
        ),
        ItemShowParam(
            StringUtils.getString(R.string.device_appointment_setting),
            R.mipmap.icon_device_device_appointment_setting,
            hasAppointment
        ),
    )

    fun requestData() {
        if (-1 == goodsId) {
            return
        }

        launch({
            val detail = ApiRepository.dealApiResult(mDeviceRepo.deviceDetail(goodsId))
            detail?.let {
                deviceDetail.postValue(detail)
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

}