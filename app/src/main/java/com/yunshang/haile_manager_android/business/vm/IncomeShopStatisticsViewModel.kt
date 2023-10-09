package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.ShopRevenueDetailEntity
import com.yunshang.haile_manager_android.data.entities.TotalRevenueEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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
class IncomeShopStatisticsViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

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
        callBack: (MutableList<ShopRevenueDetailEntity>?) -> Unit
    ) {
        launch({
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
            // 请求店铺列表数据
            val list = ApiRepository.dealApiResult(
                mCapitalRepo.requestShopRevenueDetail(
                    getCommonParams()
                )
            )
            withContext(Dispatchers.Main) {
                callBack(list)
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                callBack.invoke(null)
            }
        })
    }

    private fun getCommonParams(page: Int? = null, categoryList: List<String>? = null) =
        ApiRepository.createRequestBody(
            hashMapOf(
                "shopIdList" to shopIds,
                "categoryCodeList" to (categoryList ?: categoryCodes),
                "startTime" to DateTimeUtils.formatDateTimeStartParam(startDate.value),
                "endTime" to DateTimeUtils.formatDateTimeEndParam(endDate.value),
            ).also { params ->
                page?.let {
                    params["page"] = page
                    params["pageSize"] = 20
                }
            }
        )

    fun requestDeviceList(context: Context, item: ShopRevenueDetailEntity, callBack: () -> Unit) {
        launch({
            if (0 == item.page) {
                item.page = 1
            }
            ApiRepository.dealApiResult(
                mCapitalRepo.requestShopDeviceRevenueList(
                    getCommonParams(item.page, listOf(item.categoryCode))
                )
            )?.let {
                if (null == item.deviceList) {
                    item.deviceList = mutableListOf()
                }
                it.items?.let { list ->
                    item.deviceList!!.addAll(list)
                    if (list.size < 20) {
                        item.noMore = true
                    } else {
                        item.page++
                        item.noMore = false
                    }
                } ?: run { item.noMore = false }
                withContext(Dispatchers.Main) {
                    callBack()
                }
            }
        }, {
            item.noMore = false
            withContext(Dispatchers.Main) {
                SToast.showToast(context, it.message ?: "")
                callBack()
            }
        })
    }
}