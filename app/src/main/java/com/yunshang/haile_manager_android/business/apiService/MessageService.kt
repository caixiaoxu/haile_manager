package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 15:11
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface MessageService {

    /**
     * 消息数量接口
     */
    @POST("/message/count")
    suspend fun messageCount(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 各类型消息数量接口
     */
    @POST("/message/typeCount")
    suspend fun messageTypeCount(@Body body: RequestBody): ResponseWrapper<List<MessageTypeCountEntity>>

    /**
     * 消息列表接口
     */
    @POST("/message/list")
    suspend fun messageList(@Body body: RequestBody): ResponseWrapper<ResponseList<MessageEntity>>

    /**
     * 获取消息二级类型列表接口
     */
    @POST("/message/subtype/list")
    suspend fun requestSubTypeList(@Body body: RequestBody): ResponseWrapper<MutableList<MessageSubTypeEntity>>

    /**
     * 消息设置列表接口
     */
    @POST("/message/settings")
    suspend fun requestMessageSettingList(@Body body: RequestBody): ResponseWrapper<MutableList<MessageSettingEntity>>

    /**
     * 消息设置接口
     */
    @POST("/message/setting")
    suspend fun requestMessageSetting(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 消息一键已读接口
     */
    @POST("/message/readAll")
    suspend fun readMessageAll(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 各种类型消息数量接口
     */
    @POST("/icon/list")
    suspend fun requestTypeMsgNum(): ResponseWrapper<TypeMsgNumListEntity>
}