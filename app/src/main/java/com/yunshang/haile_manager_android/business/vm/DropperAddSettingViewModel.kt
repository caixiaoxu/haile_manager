package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.DeviceCreateParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.ExtAttrBean
import com.yunshang.haile_manager_android.data.entities.SkuEntity
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
class DropperAddSettingViewModel : BaseViewModel() {

    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    val configurationList: MutableLiveData<MutableList<SkuEntity>> by lazy {
        MutableLiveData()
    }


    // goodsId
    var goodsId: Int = -1

    // spuid
    var spuId: Int = -1

    /**
     * 获取数据
     */
    fun requestData() {
        if (-1 == spuId) {
            return
        }
        launch({
            val list = ApiRepository.dealApiResult(mRepo.sku(spuId))
            list?.let {
                it.forEach { sku ->
                    if (sku.extAttr.isNotEmpty()) {
                        sku.extAttrValue =
                            GsonUtils.json2List(sku.extAttr, ExtAttrBean::class.java)?.apply {
                                forEach { ext ->
                                    ext.isCheck = true
                                }
                            }
                    }
                }
                configurationList.postValue(it)
            }
        })
    }


    /**
     * 提交语音设置
     */
    fun submit(
        imei: String,
        volume: Int,
        voiceBroadcastStatus: Boolean,
        preventDisturbSwitch: Boolean,
        preventDisturbStartTime: String,
        preventDisturbStopTime: String
    ) {
        if (imei.isNullOrEmpty()) {
            return
        }
        if (voiceBroadcastStatus && preventDisturbSwitch && preventDisturbStartTime.isNullOrEmpty() && preventDisturbStopTime.isNullOrEmpty()) {
            SToast.showToast(msg = "请选择时间")
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mRepo.deviceVolumeSetting(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "imei" to imei,
                            "volume" to volume,
                            "preventDisturbSwitch" to preventDisturbSwitch,
                            "voiceBroadcastStatus" to voiceBroadcastStatus,
                            "preventDisturbStartTime" to preventDisturbStartTime,
                            "preventDisturbStopTime" to preventDisturbStopTime,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(msg = "操作成功")
            }
            jump.postValue(0)
        })
    }


}