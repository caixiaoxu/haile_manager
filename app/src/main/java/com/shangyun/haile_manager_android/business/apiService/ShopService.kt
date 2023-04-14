package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.HomeIncomeEntity
import com.shangyun.haile_manager_android.data.entities.ShopEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Title : 店铺接口
 * Author: Lsy
 * Date: 2023/3/17 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface ShopService {

    /**
     * 店铺列表接口
     */
    @POST("/shop/shopList")
    suspend fun shopList(@Body body: RequestBody): ResponseWrapper<ResponseList<ShopEntity>>
}