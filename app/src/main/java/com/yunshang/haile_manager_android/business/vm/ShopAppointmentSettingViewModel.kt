package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.AppointmentSettingEntity
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
class ShopAppointmentSettingViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    // 选择的门店
    val selectShops: MutableLiveData<MutableList<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val selectShopNames: LiveData<String> = selectShops.map {
        when (val size = it.size) {
            0 -> ""
            1 -> it.firstOrNull()?.name ?: ""
            else -> "已选择${size}个门店"
        }
    }

    val appointmentSetting: MutableLiveData<AppointmentSettingEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (selectShops.value.isNullOrEmpty()) return

        launch({
            ApiRepository.dealApiResult(mRepo.requestShopBatchAppointmentSettingList())
                ?.let { result ->
                    appointmentSetting.postValue(result.apply {
                        settingList = settings
                        settings = null
                    })
                }
        })
    }

    /**
     * 保存
     */
    fun save(context: Context) {
        val checkTime = appointmentSetting.value?.checkTime
        if (null == checkTime || checkTime < 3 || checkTime > 30) {
            SToast.showToast(context, "请选择选择3-30之间的验证时长")
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mRepo.setShopAppointment(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(appointmentSetting.value?.apply {
                            shopIdList = selectShops.value?.map { it.id }?.toIntArray()
                        })
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.save_success)
            }
            jump.postValue(0)
        })
    }
}