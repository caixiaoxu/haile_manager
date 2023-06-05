package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.MessageEntity
import com.yunshang.haile_manager_android.data.entities.MessageTypeCountEntity
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
     * 各类型消息数量接口
     */
    @POST("/message/typeCount")
    suspend fun messageTypeCount(@Body body: RequestBody): ResponseWrapper<List<MessageTypeCountEntity>>

    /**
     * 消息列表接口
     */
    @POST("/message/list")
    suspend fun messageList(@Body body: RequestBody): ResponseWrapper<ResponseList<MessageEntity>>
}