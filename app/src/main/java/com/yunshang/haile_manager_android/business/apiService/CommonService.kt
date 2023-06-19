package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.entities.AreaEntity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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

    @GET("/common/appVersion")
    suspend fun appVersion(
        @Query("currentVersion") currentVersion: String,
        @Query("appType") appType: Int = 2
    ): ResponseWrapper<AppVersionEntity>

    @GET("/common/businessForH5")
    suspend fun requestWhiteList(): ResponseWrapper<MutableList<String>>
}