package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.NoticeDetailEntity
import com.yunshang.haile_manager_android.data.entities.NoticeEntity
import com.yunshang.haile_manager_android.data.entities.NoticeTemplateEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Title :
 * Author: Gdz
 * Date: 2023/7/19 14:04
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface NoticeService {

    /**
     * 公告列表接口
     */
    @POST("/notice/noticeList")
    suspend fun requestNoticeList(@Body body: RequestBody): ResponseWrapper<ResponseList<NoticeEntity>>

    /**
     * 公告模板列表接口
     */
    @POST("/notice/templateSelectList")
    suspend fun requestNoticeTemplateList(@Body body: RequestBody): ResponseWrapper<List<NoticeTemplateEntity>>

    /**
     * 提交公告接口
     */
    @POST("/notice/addNotice")
    suspend fun requestAddNotice(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 删除公告接口
     */
    @POST("/notice/endNotice")
    suspend fun requestEndNotice(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 公告詳情接口
     */
    @POST("/notice/noticeDetail")
    suspend fun requestNoticeDetail(@Body body: RequestBody): ResponseWrapper<NoticeDetailEntity>

    /**
     * 公告修改接口
     */
    @POST("/notice/editNotice")
    suspend fun requestEditNotice(@Body body: RequestBody): ResponseWrapper<Any>


}