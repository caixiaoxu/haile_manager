package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.MessageService
import com.yunshang.haile_manager_android.data.entities.MessageContentEntity
import com.yunshang.haile_manager_android.data.entities.MessageSubTypeEntity
import com.yunshang.haile_manager_android.data.entities.MessageSystemContentEntity
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
                subTypeList.forEach {
                    val msgList = MessageCenterEntity(it.typeId, it.name)
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
                        if (msgs.items.isNotEmpty()) {
                            msgList.isNull = false
                            msgList.time = DateTimeUtils.getFriendlyTime(
                                DateTimeUtils.formatDateFromString(msgs.items[0].createTime), false
                            )

                            if (msgs.items[0].subtype == "merchant:device:fault") {
                                GsonUtils.json2Class(
                                    msgs.items[0].content,
                                    MessageContentEntity::class.java
                                )?.let { content ->
                                    msgList.last = content.shortDescription
                                }
                            } else if (msgs.items[0].subtype == "merchant:system:system") {
                                GsonUtils.json2Class(
                                    msgs.items[0].content,
                                    MessageSystemContentEntity::class.java
                                )?.let { content ->
                                    msgList.last = content.shortDescription
                                }
                            }
                        }
                    }

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
                        }
                    }
                    list.add(msgList)
                }
                messageList.postValue(list)
            }
        })
    }

    class MessageCenterEntity(val typeId: Int, val title: String) {
        val typeIcon: Int
            get() = when (typeId) {
                1 -> R.mipmap.icon_message_malfunction
                2 -> R.mipmap.icon_message_system
                else -> R.mipmap.ic_launcher
            }

        var time: String = ""
        var last: String = ""
        var count: Int = 0
        var isNull: Boolean = true

        fun getLastMsg() = if (isNull) StringUtils.getString(R.string.message_empty) else last
    }

    fun readAllMessage(typeId: Int? = null) {
        launch({
            ApiRepository.dealApiResult(
                mMessageRepo.readMessageAll(
                    ApiRepository.createRequestBody(
                        hashMapOf<String, Any>().apply {
                            typeId?.let {
                                put("typeId", typeId)
                            }
                        }
                    )
                )
            )
            messageList.value?.let { list ->
                val temp = mutableListOf<MessageCenterEntity>()
                list.forEach { it.count = 0 }
                temp.addAll(list)
                messageList.postValue(temp)
            }
        })
    }
}