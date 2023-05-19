package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.business.apiService.SubAccountService
import com.shangyun.haile_manager_android.data.entities.SubAccountEntity
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
class SubAccountManagerViewModel : BaseViewModel() {
    private val mSubAccountRepo = ApiRepository.apiClient(SubAccountService::class.java)

    val keyword: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val subAccountList: MutableLiveData<MutableList<SubAccountEntity>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            ApiRepository.dealApiResult(mSubAccountRepo.requestSubAccountList(keyword.value ?: ""))
                ?.let {
                    subAccountList.postValue(it)
                }
        })
    }
}