package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.data.entities.*
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

    @GET("/common/appVersion")
    suspend fun appVersion(
        @Query("currentVersion") currentVersion: String,
        @Query("appType") appType: Int = 2
    ): ResponseWrapper<AppVersionEntity>

    @GET("/common/businessForH5")
    suspend fun requestWhiteList(): ResponseWrapper<MutableList<String>>

    @GET(BuildConfig.SERVICE_CHECK)
    suspend fun checkService(): Int

    /**
     * 银行列表
     */
    @POST("/bank/bank/list")
    suspend fun bankList(@Body params: RequestBody): ResponseWrapper<ResponseList<BankEntity>>

    /**
     * 支行列表
     */
    @POST("/bank/subBank/list")
    suspend fun subBankList(@Body params: RequestBody): ResponseWrapper<ResponseList<SubBankEntity>>

}