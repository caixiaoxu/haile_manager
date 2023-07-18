package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.SubAccountService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.SubAccountDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
class SubAccountDetailViewModel : BaseViewModel() {
    private val mSubAccountRepo = ApiRepository.apiClient(SubAccountService::class.java)

    var userId: Int = -1

    val subAccountDetail: MutableLiveData<SubAccountDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == userId) return
        launch({
            ApiRepository.dealApiResult(mSubAccountRepo.requestSubAccountDetail(userId))?.let {
                subAccountDetail.postValue(it)
            }
        })
    }

    fun deleteSubAccount(context: Context, settingId: Int) {
        launch({
            ApiRepository.dealApiResult(
                mSubAccountRepo.deleteSubAccount(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to settingId
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.delete_success)
            }
            requestData()
        })
    }
}