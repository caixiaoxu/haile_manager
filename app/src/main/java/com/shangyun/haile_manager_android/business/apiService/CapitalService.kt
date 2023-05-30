package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
     * 总收益接口
     */
    @POST("/profit/totalIncome")
    suspend fun totalIncome(@Body body: RequestBody): ResponseWrapper<String>

    /**
     * 收益列表接口
     */
    @POST("/profit/incomeByDate")
    suspend fun incomeByDate(@Body body: RequestBody): ResponseWrapper<List<IncomeCalendarEntity>>

    /**
     * 收益明细列表接口
     */
    @POST("/profit/profitOrderList")
    suspend fun incomeListByDay(@Body body: RequestBody): ResponseWrapper<ResponseList<IncomeListByDayEntity>>

    /**
     * 收益明细详情接口
     */
    @GET("/profit/profitDetail")
    suspend fun incomeDetail(@Query("id") id: Int): ResponseWrapper<IncomeDetailEntity>

    /**
     * 充值详情接口
     */
    @GET("/starfish/getStarFishLogVO")
    suspend fun rechargeDetail(
        @Query("orderNo") orderNo: String,
        @Query("id") id: Int? = null
    ): ResponseWrapper<RechargeDetailEntity>

    /**
     * 首页收益趋势接口
     */
    @POST("/homePage/homeIncome")
    suspend fun homeInCome(@Body body: RequestBody): ResponseWrapper<List<HomeIncomeEntity>>

    /**
     * 余额明细列表接口
     */
    @GET("/wallet/balance/list")
    suspend fun balanceList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String,
    ): ResponseWrapper<ResponseList<BalanceEntity>>

    /**
     * 余额明细详情接口
     */
    @GET("/wallet/balance/detail")
    suspend fun balanceDetail(
        @Query("id") id: Int,
    ): ResponseWrapper<BalanceDetailEntity>

}