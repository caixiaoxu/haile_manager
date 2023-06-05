package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.IncomeDetailEntity
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
class EarningsDetailViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    var incomeId: Int = -1

    val incomeDetail: MutableLiveData<IncomeDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == incomeId) return
        launch({
            ApiRepository.dealApiResult(mCapitalRepo.incomeDetail(incomeId))?.let {
                incomeDetail.postValue(it)
            }
        })
    }
}