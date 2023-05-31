package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/31 15:10
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class UpdateValueViewModel : BaseViewModel() {
    private val mUserService = ApiRepository.apiClient(LoginUserService::class.java)

    val updateVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun updatePersonalName(view: View, callback: () -> Unit) {
        if (updateVal.value.isNullOrEmpty()) {
            SToast.showToast(view.context, R.string.update_empty_hint)
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mUserService.updateUserInfo(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "nickName" to updateVal.value!!,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(view.context, R.string.update_success)
                callback()
            }
        })
    }

}