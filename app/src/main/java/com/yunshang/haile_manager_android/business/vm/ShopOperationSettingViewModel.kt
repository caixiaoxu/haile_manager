package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.ShopOperationSettingEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/4 18:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopOperationSettingViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    var shopId: Int = -1

    val operationSettingDetail: MutableLiveData<ShopOperationSettingEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == shopId) return
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.requestOperationSettingDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf("id" to shopId)
                    )
                )
            )?.let {
                operationSettingDetail.postValue(it.apply {
                    appointSetting?.settingList = appointSetting?.settings
                    appointSetting?.settings = null
                    freeSelfClearSettingsForm = freeSelfClearSettingDetailDTO
                    freeSelfClearSettingDetailDTO = null

                    showItem1 = null != paymentSetting
                    showItem2 = null != compensationSetting
                    showItem3 = !appointSetting?.settingList.isNullOrEmpty()
                    showItem4 = null != operationSetting
                    showItem5 = null != freeSelfClearSettingsForm
                })
            }
        })
    }

    fun save(context: Context) {
        val checkTime = operationSettingDetail.value?.appointSetting?.checkTime
        if (null == checkTime || checkTime < 3 || checkTime > 30) {
            SToast.showToast(context, "请选择选择3-30之间的验证时长")
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mShopRepo.saveOperationSetting(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(
                            operationSettingDetail.value
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.save_success)
            }
            LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}