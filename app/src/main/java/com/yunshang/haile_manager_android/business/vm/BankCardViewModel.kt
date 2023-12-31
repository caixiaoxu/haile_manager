package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.entities.BankCardEntity
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
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
class BankCardViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    private val mUserRepo = ApiRepository.apiClient(LoginUserService::class.java)

    var realNameAuthDetail: RealNameAuthDetailEntity? = null

    val bankCard: MutableLiveData<BankCardEntity?> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            if (null == realNameAuthDetail) {
                realNameAuthDetail =
                    ApiRepository.dealApiResult(mUserRepo.requestRealNameAuthDetail())
            }

            ApiRepository.dealApiResult(
                mCapitalRepo.requestBankCardList(
                    ApiRepository.createRequestBody("")
                )
            )?.let {
                bankCard.postValue(it.items?.firstOrNull())
            }
        })
    }
}