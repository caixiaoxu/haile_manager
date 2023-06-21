package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.MessageService
import com.yunshang.haile_manager_android.data.entities.MessageEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/21 17:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MessageCenterViewModel : BaseViewModel() {
    private val mMessageRepo = ApiRepository.apiClient(MessageService::class.java)

    private val messageList: MutableLiveData<MutableList<MessageEntity>> by lazy {
        MutableLiveData()
    }

    /**
     * 刷新设备列表
     */
    fun requestMessageList() {
        launch({
            ApiRepository.dealApiResult(
                mMessageRepo.requestSubTypeList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "appType" to 1
                        )
                    )
                )
            )?.let { subTypeList ->
                subTypeList.forEach {
                    ApiRepository.dealApiResult(
                        mMessageRepo.messageList(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "pageNum" to 1,
                                    "pageSize" to 1,
                                    "appType" to 1,
                                    "typeId" to it.typeId,
                                )
                            )
                        )
                    )?.let { msgs ->
                        messageList.postValue(mutableListOf<MessageEntity>().apply {
                            messageList.value?.let { list ->
                                addAll(list)
                            }
                            if (msgs.items.isNotEmpty()) {
                                add(msgs.items[0])
                            }
                        })
                    }
                }
            }
        })
    }
}