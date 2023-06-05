package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Title : 设备接口
 * Author: Lsy
 * Date: 2023/4/23 10:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface CategoryService {

    /**
     * 分类接口
     */
    @GET("/category/list")
    suspend fun category(@Query("parentId") parentId: Int): ResponseWrapper<List<CategoryEntity>>

}