package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
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
import com.yunshang.haile_manager_android.data.extend.isGreaterThan0
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
    var isBatch: Boolean = false

    // 选择的店铺
    val selectShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 选择的店铺列表
    val selectShops: MutableLiveData<List<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val createUpdateParams: MutableLiveData<HaixinSchemeConfigCreateParams> =
        MutableLiveData(HaixinSchemeConfigCreateParams())

    fun switchSchemeOpen(isCheck: Boolean) {
        createUpdateParams.value?.isAllowRefund = if (isCheck) 1 else 0
    }

    fun requestSchemeDetail() {
        if (!isBatch && !createUpdateParams.value?.shopId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestSchemeDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to if (isBatch) 0 else createUpdateParams.value!!.shopId!!
                        )
                    )
                )
            )?.let {
                createUpdateParams.postValue(HaixinSchemeConfigCreateParams().apply {
                    id = it.id
                    shopId =
                        if (isBatch) null else if (-1 == it.shopId || 0 == it.shopId) createUpdateParams.value?.shopId else it.shopId
                    shopIds = if (isBatch) selectShops.value?.map { item -> item.id } else null
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
            if (isBatch) {
                if (it.shopIds.isNullOrEmpty()) {
                    SToast.showToast(v.context, R.string.empty_scheme_shop)
                    return@let
                }
            } else {
                if (-1 == it.shopId || 0 == it.shopId) {
                    SToast.showToast(v.context, R.string.empty_scheme_shop)
                    return@let
                }
            }
            if (!it.rewards.any { item -> 0 == item.status && item.reach.isGreaterThan0() }) {
                SToast.showToast(v.context, R.string.empty_scheme_list)
                return@let
            }

            launch({
                val params = ApiRepository.createRequestBody(GsonUtils.any2Json(it))

                if (isBatch) {
                    ApiRepository.dealApiResult(
                        mHaiXinRepo.batchSettingSchemeConfig(params)
                    )
                    withContext(Dispatchers.Main) {
                        SToast.showToast(v.context, "批量设置成功")
                        LiveDataBus.post(BusEvents.HAIXIN_SCHEME_LIST_STATUS, true)
                    }
                } else {
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
                }
                jump.postValue(0)
            })
        }
    }
}