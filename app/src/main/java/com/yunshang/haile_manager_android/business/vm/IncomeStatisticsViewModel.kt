package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
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
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    private var page = 1

    // 开始和结束日期
    val startDate: MutableLiveData<Date> = MutableLiveData(Date())
    val endDate: MutableLiveData<Date> = MutableLiveData(Date())
    val dateSpace: MediatorLiveData<String> = MediatorLiveData("").apply {
        addSource(startDate) {
            value = mergeDate()
        }
        addSource(endDate) {
            value = mergeDate()
        }
    }

    private fun mergeDate(): String = if (DateTimeUtils.isSameDay(startDate.value, endDate.value)) {
        "${DateTimeUtils.formatDateTime(startDate.value, "MM-dd")}"
    } else {
        val formatStr =
            if (DateTimeUtils.isSameYear(
                    startDate.value,
                    endDate.value
                )
            ) "MM-dd" else "yyyy-MM-dd"

        "${DateTimeUtils.formatDateTime(startDate.value, formatStr)} 至 ${
            DateTimeUtils.formatDateTime(
                endDate.value,
                formatStr
            )
        }"
    }

    // 店铺
    var shopIds: List<Int>? = null

    // 设备类型
    var categoryCodes: List<String>? = null

    // 设备类型
    val categoryList: MutableLiveData<MutableList<CategoryEntity>> by lazy {
        MutableLiveData()
    }

    val totalRevenue: MutableLiveData<TotalRevenueEntity> by lazy {
        MutableLiveData()
    }

    fun requestData(
        type: Int,
        isRefresh: Boolean,
        callBack: (MutableList<ShopRevenueEntity>) -> Unit
    ) {
        launch({
            if (isRefresh) {
                if (0 == type) {
                    ApiRepository.dealApiResult(
                        mCategoryRepo.category(1)
                    )?.let {
                        it.add(
                            0,
                            CategoryEntity(
                                id = 0,
                                name = StringUtils.getString(R.string.all_device)
                            ).apply {
                                onlyOne = true
                            })
                        categoryList.postValue(it)
                    }
                }

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
            "categoryCodeList" to categoryCodes,
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