package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.OrderService
import com.yunshang.haile_manager_android.data.entities.OrderExecutiveLoggingEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/30 09:43
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class OrderExecutiveLoggingViewModel : BaseViewModel() {
    private val mOrderRepo = ApiRepository.apiClient(OrderService::class.java)

    var orderId = -1
    var orderNo: String? = ""

    val executiveLoggingList: MutableLiveData<MutableList<OrderExecutiveLoggingEntity>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == orderId) return

        launch({
            executiveLoggingList.postValue(
                ApiRepository.dealApiResult(
                    mOrderRepo.requestOrderExecutiveLoggingList(orderId)
                )
            )
        })
    }
}