package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.*

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
    @POST("/profit/totalIncomeToday/v2")
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
     * 获取收支明细列表接口
     */
    @POST("/profitStatistics/getProfitStatisticsListVO")
    suspend fun requestProfitStatisticsList(@Body body: RequestBody): ResponseWrapper<ResponseList<ProfitStatisticsEntity>>

    /**
     * 收益明细详情接口
     */
    @GET("/profit/profitDetail")
    suspend fun incomeDetail(@Query("id") id: Int): ResponseWrapper<IncomeDetailEntity>


    /**
     * 首页收益趋势接口
     */
    @POST("/profit/homeIncome/v2")
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
    @POST("/wallet/cashOutAccount/info/v2")
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
    @POST("/wallet/cashOut/calculate/v3")
    suspend fun calculateWithdraw(@Body body: RequestBody): ResponseWrapper<WithdrawCalculateEntity>

    /**
     * 提现接口
     */
    @POST("/wallet/cashOut/withdraw/v3")
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

    /**
     * 银行卡添加接口
     */
    @POST("/candyPay/bankCard/create")
    suspend fun createBankCard(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 银行卡修改接口
     */
    @POST("/candyPay/bankCard/modify")
    suspend fun updateBankCardDetail(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 银行卡详情接口
     */
    @POST("/candyPay/bankCard/detail")
    suspend fun requestBankCardDetail(@Body body: RequestBody): ResponseWrapper<BankCardDetailEntity>

    /**
     * 银行卡解绑接口
     */
    @POST("/candyPay/bankCard/release")
    suspend fun requestBankCardRelease(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 银行卡删除接口
     */
    @POST("/candyPay/bankCard/delete")
    suspend fun requestBankCardDelete(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 收益明细总收益接口
     */
    @POST("/profitStatistics/getTotalRevenue")
    suspend fun requestTotalRevenue(@Body body: RequestBody): ResponseWrapper<TotalRevenueEntity>

    /**
     * 收益店铺列表接口
     */
    @POST("/profitStatistics/getShopRevenueList")
    suspend fun requestShopRevenueList(@Body body: RequestBody): ResponseWrapper<ResponseList<ShopRevenueEntity>>

    /**
     * 收益店铺详情接口
     */
    @POST("/profitStatistics/getShopDetailStatistics")
    suspend fun requestShopRevenueDetail(@Body body: RequestBody): ResponseWrapper<MutableList<ShopRevenueDetailEntity>>

    /**
     * 收益总收支接口
     */
    @POST("/profitStatistics/getProfitStatisticsVO")
    suspend fun requestTotalIncomeExpenses(@Body body: RequestBody): ResponseWrapper<TotalIncomeExpensesEntity>

    /**
     * 收益收支明细列表接口
     */
    @POST("/profitStatistics/getProfitStatisticsListVO")
    suspend fun requestIncomeExpensesDetailList(@Body body: RequestBody): ResponseWrapper<ResponseList<IncomeExpensesDetailEntity>>

    /**
     * 设备收益列表接口
     */
    @POST("/profitStatistics/getDeviceProfitVOPage")
    suspend fun requestShopDeviceRevenueList(@Body body: RequestBody): ResponseWrapper<ResponseList<ShopDeviceRevenueListEntity>>

    /**
     * 提现导出接口
     */
    @POST("/export/task")
    suspend fun exportWithdraw(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 提现导出接口
     */
    @POST("/export/taskList")
    suspend fun requestExportHistory(@Body body: RequestBody): ResponseWrapper<ResponseList<ExportHistoryEntity>>

    /**
     * 发票提现手续费接口
     */
    @POST("/invoice/cashOut/list")
    suspend fun requestInvoiceCashOutList(@Body body: RequestBody): ResponseWrapper<ResponseList<InvoiceWithdrawFeeEntity>>

    /**
     * 发票提现人列表接口
     */
    @POST("/invoice/user/list")
    suspend fun requestInvoiceUserList(): ResponseWrapper<MutableList<InvoiceUserEntity>>

    /**
     * 发票抬头列表接口
     */
    @POST("/invoice/template/list")
    suspend fun requestInvoiceTitleList(@Body body: RequestBody): ResponseWrapper<MutableList<InvoiceTitleEntity>>

    /**
     * 发票抬头新增接口
     */
    @POST("/invoice/template/create")
    suspend fun createInvoiceTitle(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 发票抬头修改接口
     */
    @POST("/invoice/template/edit")
    suspend fun updateInvoiceTitle(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 发票抬头修改接口
     */
    @POST("/invoice/template/detail/{id}")
    suspend fun requestInvoiceTitleDetails(@Path("id") id: Int): ResponseWrapper<InvoiceTitleEntity>

    /**
     * 发票抬头删除接口
     */
    @POST("/invoice/template/delete")
    suspend fun deleteInvoiceTitle(@Body body: RequestBody): ResponseWrapper<Any>

    /**
     * 开票列表接口
     */
    @POST("/invoice/list")
    suspend fun requestInvoiceList(@Body body: RequestBody): ResponseWrapper<ResponseList<IssueInvoiceDetailsEntity>>

    /**
     * 开票详情接口
     */
    @POST("/invoice/detail/{id}")
    suspend fun requestInvoiceDetails(@Path("id") id: Int): ResponseWrapper<IssueInvoiceDetailsEntity>

}