package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.HomeIncomeEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Title : 资金接口
 * Author: Lsy
 * Date: 2023/3/17 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface CapitalService {

    /**
     * 当日总收益接口
     */
    @POST("/profit/totalIncomeToday")
    suspend fun totalIncomeToady(): ResponseWrapper<String>

    /**
     * 首页收益趋势接口
     */
    @POST("/homePage/homeIncome")
    suspend fun homeInCome(@Body body: RequestBody): ResponseWrapper<List<HomeIncomeEntity>>

}