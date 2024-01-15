package com.yunshang.haile_manager_android.business.vm

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.MessageService
import com.yunshang.haile_manager_android.data.entities.MessageSubTypeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils

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

    val messageList: MutableLiveData<MutableList<MessageCenterEntity>> by lazy {
        MutableLiveData()
    }

    var subTypeList = mutableListOf<MessageSubTypeEntity>()

    val totalUnReadMsgNum = MutableLiveData(0)

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
                this.subTypeList = subTypeList
                val list = mutableListOf<MessageCenterEntity>()
                var total = 0
                subTypeList.forEach {
                    val msgList = MessageCenterEntity(
                        it.id, it.typeId, it.name, it.iconUrl, DateTimeUtils.getFriendlyTime(
                            DateTimeUtils.formatDateFromString(it.lastMessageTime)
                        )
                    )
                    ApiRepository.dealApiResult(
                        mMessageRepo.messageTypeCount(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "appType" to 1,
                                    "typeId" to it.typeId,
                                    "readStatus" to 0
                                )
                            )
                        )
                    )?.let { list ->
                        if (list.isNotEmpty()) {
                            msgList.count = list[0].count
                            total += list[0].count
                        }
                    }
                    list.add(msgList)
                }
                messageList.postValue(list)
                totalUnReadMsgNum.postValue(total)
            }
        })
    }

    class MessageCenterEntity(
        val id: Int,
        val typeId: Int,
        val title: String,
        val iconUrl: String?,
        val time: String? = null
    ) :
        BaseObservable() {
        val typeIcon: Int
            get() = when (typeId) {
                1 -> R.mipmap.icon_message_malfunction
                2 -> R.mipmap.icon_message_system
                else -> R.mipmap.ic_launcher
            }

        @get:Bindable
        var count: Int = 0
            set(value) {
                field = value
                notifyPropertyChanged(BR.count)
                notifyPropertyChanged(BR.countVal)
            }

        @get:Bindable
        val countVal: String
            get() = if (count > 99) "99+" else "$count"
    }

    fun readAllMessage(typeId: Int? = null, subtypeId: Int? = null) {
        launch({
            ApiRepository.dealApiResult(
                mMessageRepo.readMessageAll(
                    ApiRepository.createRequestBody(
                        hashMapOf<String, Any>().apply {
                            typeId?.let {
                                put("typeId", typeId)
                            }
                            subtypeId?.let {
                                put("subtypeId", subtypeId)
                            }
                        }
                    )
                )
            )
            messageList.value?.forEach { it.count = 0 }
            totalUnReadMsgNum.postValue(0)
        })
    }
}