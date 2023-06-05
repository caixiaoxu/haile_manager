package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.DiscountsService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.DiscountsDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

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
class DiscountsDetailViewModel : BaseViewModel() {
    private val mDiscountsRepo = ApiRepository.apiClient(DiscountsService::class.java)
    var discountId: Int = -1

    var expired: Int = -1

    val discountsDetail: MutableLiveData<DiscountsDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestDiscountsDetail() {
        if (-1 == discountId) {
            return
        }
        launch({
            ApiRepository.dealApiResult(mDiscountsRepo.requestDiscountsDetail(discountId))?.let {
                discountsDetail.postValue(it)
            }
        })
    }

    fun deleteConfig(view: View) {
        if (-1 == discountId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mDiscountsRepo.deleteDiscountsConfig(hashMapOf("id" to discountId))
            )
            LiveDataBus.post(BusEvents.DISCOUNTS_LIST_ITEM_DELETE_STATUS, discountId)
            jump.postValue(0)
        })
    }
}