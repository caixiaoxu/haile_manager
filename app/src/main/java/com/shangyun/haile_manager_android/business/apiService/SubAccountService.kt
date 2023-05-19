package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.*
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/11 19:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface SubAccountService {

    /**
     * 请求分账列表
     */
    @GET("/funds/distribution/setting/list")
    suspend fun requestSubAccountList(@Query("keywords") keywords: String): ResponseWrapper<MutableList<SubAccountEntity>>
}