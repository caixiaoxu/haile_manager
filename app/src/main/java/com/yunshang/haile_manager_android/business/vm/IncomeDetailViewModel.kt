package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.IIncomeDetailEntity

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
    private val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    var detailType: Int = 0 // 0收入、1海星用户 2海星充值
    var incomeId: Int = -1
    var orderNo: String? = null

    val incomeDetail: MutableLiveData<IIncomeDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (1 == detailType) {
            requestReChargeDetailData()
        } else if (2 == detailType) {
            requestReChargeOrderDetailData()
        } else {
            requestIncomeDetailData()
        }
    }

    fun requestIncomeDetailData() {
        if (-1 == incomeId) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.balanceDetail(incomeId)
            )?.let {
                incomeDetail.postValue(it)
            }
        })
    }

    fun requestReChargeDetailData() {
        if (orderNo.isNullOrEmpty() && -1 == incomeId) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.rechargeDetail(
                    orderNo,
                    if (-1 == incomeId) null else incomeId
                )
            )?.let {
                incomeDetail.postValue(it)
            }
        })
    }

    fun requestReChargeOrderDetailData() {
        if (orderNo.isNullOrEmpty() && -1 == incomeId) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.rechargeOrderDetail(
                    orderNo,
                    if (-1 == incomeId) null else incomeId
                )
            )?.let {
                incomeDetail.postValue(it)
            }
        })
    }
}