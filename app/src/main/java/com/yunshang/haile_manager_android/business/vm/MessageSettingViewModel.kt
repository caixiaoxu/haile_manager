package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.MessageService
import com.yunshang.haile_manager_android.data.entities.MessageSettingEntity
import com.yunshang.haile_manager_android.data.entities.MessageSubTypeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/26 20:23
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MessageSettingViewModel : BaseViewModel() {
    private val mMessageRepo = ApiRepository.apiClient(MessageService::class.java)

    val subTypeList: MutableLiveData<MutableList<MessageSettingEntity>> by lazy {
        MutableLiveData()
    }

    fun requestSubTypeList() {
        launch({
            ApiRepository.dealApiResult(
                mMessageRepo.requestMessageSettingList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "appType" to 1
                        )
                    )
                )
            )?.let {
                subTypeList.postValue(it)
            }
        })
    }

    fun requestMessageSetting(subtypeId: Int, status: Int, callBack: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mMessageRepo.requestMessageSetting(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "subtypeId" to subtypeId,
                            "status" to status
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }
}