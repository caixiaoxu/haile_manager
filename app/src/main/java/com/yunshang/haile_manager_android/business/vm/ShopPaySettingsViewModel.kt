package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.GoodsSetting
import com.yunshang.haile_manager_android.data.entities.ShopPaySettingsEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.extend.isGreaterThan0
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/2 17:53
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopPaySettingsViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    var shopId: Int? = null
    var shopIds: IntArray? = null
    var oldShopPaySettings: ShopPaySettingsEntity? = null

    val shopPaySettings: MutableLiveData<ShopPaySettingsEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            if (null != shopIds) {
                //有shopId就请求店铺模版
                ApiRepository.dealApiResult(
                    mShopRepo.getShopPaySettingsTemplate(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "shopIdList" to shopIds
                            )
                        )
                    )
                )?.let {
                    shopPaySettings.postValue(it)
                }
            } else {
                // 有旧数据就使用，没有就重新封装
                oldShopPaySettings?.let {
                    shopPaySettings.postValue(it)
                } ?: run {
                    ApiRepository.dealApiResult(
                        mCategoryRepo.category(1)
                    )?.let {
                        shopPaySettings.postValue(ShopPaySettingsEntity(
                            1,
                            it.map { item ->
                                GoodsSetting(item.id, item.name)
                            }
                        ))
                    }
                }
            }
        })
    }

    fun submitPaySettings(callBack: () -> Unit) {
        if (null == shopIds || null == shopPaySettings.value) return
        launch({
            shopPaySettings.value?.shopIdList = shopIds!!.toList()
            ApiRepository.dealApiResult(
                mShopRepo.batchShopPaySettings(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(shopPaySettings.value)
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }

    /**
     * 修改支付设置
     */
    fun updatePaySettings(callBack: () -> Unit) {
        if (!shopId.hasVal() || null == shopPaySettings.value) return
        launch({
            shopPaySettings.value?.shopId = shopId
            ApiRepository.dealApiResult(
                mShopRepo.updateShopPaySettings(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(shopPaySettings.value)
                    )
                )
            )
            LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }
}