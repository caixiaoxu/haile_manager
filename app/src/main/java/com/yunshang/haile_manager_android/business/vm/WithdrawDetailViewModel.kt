package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
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
class WithdrawDetailViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    var id: Int = -1

    val withDrawViewModel: MutableLiveData<IIncomeDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == id) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.requestWithdrawDetail(id)
            )?.let {
                withDrawViewModel.postValue(it)
            }
        })
    }
}