package com.shangyun.haile_manager_android.data.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Path

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
interface CommService {

    @GET("/test/{param}")
    suspend fun test(@Path("param") param: String): ResponseWrapper<Array<String>>
}