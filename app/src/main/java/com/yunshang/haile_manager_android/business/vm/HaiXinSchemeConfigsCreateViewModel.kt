package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.HaixinSchemeConfigCreateParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.extend.isNotEmptyAmount
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/14 19:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HaiXinSchemeConfigsCreateViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)
    var shopId: Int = -1

    val shopName: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 选择的设备类型
    val selectShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    val createUpdateParams: MutableLiveData<HaixinSchemeConfigCreateParams> =
        MutableLiveData(HaixinSchemeConfigCreateParams())

    fun switchSchemeOpen(isCheck: Boolean) {
        createUpdateParams.value?.isAllowRefund = if (isCheck) 1 else 0
    }

    fun requestSchemeDetail() {
        if (createUpdateParams.value?.shopId?.hasVal() == false) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestSchemeDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to createUpdateParams.value!!.shopId!!
                        )
                    )
                )
            )?.let {
                createUpdateParams.postValue(HaixinSchemeConfigCreateParams().apply {
                    id = it.id
                    shopId = if (-1 == it.shopId) createUpdateParams.value?.shopId else it.shopId
                    configName =
                        it.configName.ifEmpty { createUpdateParams.value?.configName }
                    discountProportion = it.discountProportion
                    isForceUse = it.isForceUse
                    isAllowRefund = it.isAllowRefund
                    rewards = it.rewardsConfig
                    updateRewards = it.rewardsConfig
                    exchangeRate = it.exchangeRate
                })
            }
        })
    }

    fun schemeConfigSubmit(v: View) {
        createUpdateParams.value?.let {
            if (it.configName.isNullOrEmpty()) {
                SToast.showToast(v.context, R.string.empty_scheme_name)
                return@let
            }
            if (-1 == it.shopId) {
                SToast.showToast(v.context, R.string.empty_scheme_shop)
                return@let
            }
            if (!it.rewards.any { item -> 0 == item.status && item.reach.isNotEmptyAmount() && item.reward.isNotEmptyAmount() }) {
                SToast.showToast(v.context, R.string.empty_scheme_list)
                return@let
            }

            launch({
                val params = ApiRepository.createRequestBody(GsonUtils.any2Json(it))
                ApiRepository.dealApiResult(
                    if (-1 == shopId) {
                        mHaiXinRepo.createSchemeConfig(params)
                    } else {
                        mHaiXinRepo.updateSchemeConfig(params)
                    }
                )
                withContext(Dispatchers.Main) {
                    if (-1 == shopId) {
                        SToast.showToast(v.context, R.string.add_success)
                        LiveDataBus.post(BusEvents.HAIXIN_SCHEME_LIST_STATUS, true)
                    } else {
                        SToast.showToast(v.context, R.string.update_scheme)
                        LiveDataBus.post(BusEvents.HAIXIN_SCHEME_DETAIL_STATUS, true)
                    }
                }
                jump.postValue(0)
            })
        }
    }
}