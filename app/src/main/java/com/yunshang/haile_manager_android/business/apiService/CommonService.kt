package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.AreaEntity
import com.yunshang.haile_manager_android.data.entities.LoginEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface CommonService {

    /**
     * 获取区域信息
     */
    @GET("/area/getArea")
    suspend fun shopTypeList(@Query("parentId") parentId: Int): ResponseWrapper<MutableList<AreaEntity>>

    @Multipart
    @POST("/common/upload")
    suspend fun updateLoadFile(@Part file: MultipartBody.Part): ResponseWrapper<String>
}