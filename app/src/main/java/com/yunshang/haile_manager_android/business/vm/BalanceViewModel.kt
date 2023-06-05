package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.BalanceEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BalanceViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var searchDate: Date = Date()

    fun requestBalanceList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out BalanceEntity>?) -> Unit
    ) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.balanceList(
                    page,
                    pageSize,
                    DateTimeUtils.formatDateTimeStartParam(DateTimeUtils.getMonthFirst(searchDate)),
                    DateTimeUtils.formatDateTimeEndParam(DateTimeUtils.getMonthLast(searchDate)),
                )
            )?.let {
                withContext(Dispatchers.Main){
                    callBack.invoke(it)
                }
            }
        })

    }
}