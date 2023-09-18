package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.DataStatisticsShopListEntity
import com.yunshang.haile_manager_android.data.entities.ShopRevenueEntity
import com.yunshang.haile_manager_android.data.entities.TotalRevenueEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/15 15:36
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class IncomeStatisticsViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    private var page = 1

    // 开始和结束日期
    val endDate: MutableLiveData<Date> = MutableLiveData(Date())
    val startDate: MutableLiveData<Date> = MutableLiveData(DateTimeUtils.beforeMonth(Date()))
    val dateSpace: MediatorLiveData<String> = MediatorLiveData("").apply {
        addSource(startDate) {
            value = mergeDate()
        }
        addSource(endDate) {
            value = mergeDate()
        }
    }

    private fun mergeDate(): String {
        val formatStr =
            if (DateTimeUtils.isSameYear(startDate.value, endDate.value)) "MM-dd" else "yyyy-MM-dd"
        return "${DateTimeUtils.formatDateTime(startDate.value, formatStr)} 至 ${
            DateTimeUtils.formatDateTime(
                endDate.value,
                formatStr
            )
        }"
    }

    // 店铺
    var shopIds: List<Int>? = null

    // 设备类型
    var categoryIds: List<Int>? = null

    val totalRevenue: MutableLiveData<TotalRevenueEntity> by lazy {
        MutableLiveData()
    }

    fun requestData(
        isRefresh: Boolean,
        callBack: (MutableList<ShopRevenueEntity>) -> Unit
    ) {
        launch({
            if (isRefresh) {
                // 请求总收益
                ApiRepository.dealApiResult(
                    mCapitalRepo.requestTotalRevenue(
                        getCommonParams()
                    )
                )?.let {
                    totalRevenue.postValue(it)
                }
                page = 1
            }
            // 请求店铺列表数据
            ApiRepository.dealApiResult(
                mCapitalRepo.requestShopRevenueList(
                    getCommonParams(true)
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    callBack(it.items)
                }
                if (it.items.isNotEmpty()) {
                    page++
                }
            }
        })
    }

    private fun getCommonParams(needPage: Boolean = false) = ApiRepository.createRequestBody(
        hashMapOf(
            "shopIdList" to shopIds,
            "categoryCodeList" to categoryIds,
            "startTime" to DateTimeUtils.formatDateTimeStartParam(startDate.value),
            "endTime" to DateTimeUtils.formatDateTimeEndParam(endDate.value),
        ).also { params ->
            if (needPage) {
                params["page"] = page
                params["pageSize"] = 20
            }
        }
    )
}