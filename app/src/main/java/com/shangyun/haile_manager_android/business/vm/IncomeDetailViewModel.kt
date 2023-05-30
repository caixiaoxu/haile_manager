package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.CapitalService
import com.shangyun.haile_manager_android.data.entities.IncomeDetailEntity
import com.shangyun.haile_manager_android.data.entities.RechargeDetailEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository

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
class IncomeDetailViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    var incomeId: Int = -1
    var orderNo: String? = null

    val rechargeDetail: MutableLiveData<RechargeDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (orderNo.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.rechargeDetail(
                    orderNo!!,
                    if (-1 == incomeId) null else incomeId
                )
            )?.let {
                rechargeDetail.postValue(it)
            }
        })
    }
}