package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.CapitalService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.rule.IIncomeDetailEntity

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
    var detailType:Int = 0 // 0收入、1海星
    var incomeId: Int = -1
    var orderNo: String? = null

    val incomeDetail: MutableLiveData<IIncomeDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (1 == detailType){
            requestReChargeDetailData()
        } else {
            requestIncomeDetailData()
        }
    }

    fun requestIncomeDetailData(){
        if (-1 == incomeId) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.balanceDetail(incomeId)
            )?.let {
                incomeDetail.postValue(it)
            }
        })
    }

    fun requestReChargeDetailData(){
        if (orderNo.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.rechargeDetail(
                    orderNo!!,
                    if (-1 == incomeId) null else incomeId
                )
            )?.let {
                incomeDetail.postValue(it)
            }
        })
    }
}