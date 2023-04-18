package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.AreaEntity
import retrofit2.http.GET
import retrofit2.http.Query

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
}