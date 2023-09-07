package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
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
    suspend fun totalIncomeToady(@Body body: RequestBody): ResponseWrapper<String>

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
     * 首页收益趋势接口
     */
    @POST("/profit/homeIncome")
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

    /**
     * 余额接口
     */
    @GET("/wallet/balance/info")
    suspend fun requestBalance(): ResponseWrapper<BalanceTotalEntity>

    /**
     * 查询提现账号接口
     */
    @POST("/wallet/cashOutAccount/info")
    suspend fun requestWithdrawAccount(@Body body: RequestBody): ResponseWrapper<WithdrawAccountEntity>

    /**
     * 提现账号操作短信发送接口
     */
    @POST("/wallet/cashOutAccount/operateSms")
    suspend fun sendCashOutOperateSms(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 提现账号操作短信校验接口
     */
    @POST("/wallet/cashOutAccount/authCode")
    suspend fun checkCashOutOperateSms(@Body body: RequestBody): ResponseWrapper<AuthCodeEntity>

    /**
     * 绑定银行卡短信发送接口
     */
    @POST("/candyPay/bank/operateSms")
    suspend fun sendBankOperateSms(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 绑定银行卡短信校验接口
     */
    @POST("/candyPay/bank/authCode")
    suspend fun checkBankOperateSms(@Body body: RequestBody): ResponseWrapper<AuthCodeEntity>
    /**
     * 实名认证短信发送接口
     */
    @POST("/user/userVerify/operateSms")
    suspend fun sendRealNameOperateSms(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 实名认证短信校验接口
     */
    @POST("/user/userVerify/authCode")
    suspend fun checkRealNameOperateSms(@Body body: RequestBody): ResponseWrapper<AuthCodeEntity>

    /**
     * 支付宝提现账号绑定接口
     */
    @POST("/wallet/cashOutAccount/create")
    suspend fun bindWithdrawAlipayAccount(@Body body: RequestBody): ResponseWrapper<AuthCodeEntity>

    /**
     * 修改支付宝提现账号绑定接口
     */
    @POST("/wallet/cashOutAccount/modify")
    suspend fun updateWithdrawAlipayAccount(@Body body: RequestBody): ResponseWrapper<AuthCodeEntity>

    /**
     * 提现金额计算接口
     */
    @POST("/wallet/cashOut/calculate")
    suspend fun calculateWithdraw(@Body body: RequestBody): ResponseWrapper<WithdrawCalculateEntity>

    /**
     * 提现接口
     */
    @POST("/wallet/cashOut/withdraw")
    suspend fun balanceWithdraw(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 提现列表接口
     */
    @GET("/wallet/cashOut/list")
    suspend fun requestWithdrawRecordList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ResponseWrapper<ResponseList<WithdrawRecordEntity>>

    /**
     * 提现详情接口
     */
    @GET("/wallet/cashOut/detail")
    suspend fun requestWithdrawDetail(@Query("id") id: Int): ResponseWrapper<WithdrawDetailEntity>

    /**
     * 充值接口
     */
    @POST("/charge/balanceCharge")
    suspend fun balanceRecharge(@Body body: RequestBody): ResponseWrapper<BalanceRechargeEntity>

    /**
     * 预支付接口
     */
    @POST("/pay/prePay")
    suspend fun prePay(@Body body: RequestBody): ResponseWrapper<PrePayEntity>

    /**
     * 支付轮训接口
     */
    @POST("/pay/sync")
    suspend fun paySync(@Body body: RequestBody): ResponseWrapper<PaySyncEntity>

    /**
     * 银行卡列表接口
     */
    @POST("/candyPay/bankCard/list")
    suspend fun requestBankCardList(@Body body: RequestBody): ResponseWrapper<ResponseList<BankCardEntity>>


}