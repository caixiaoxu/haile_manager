package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DataStatisticsService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DataStatisticsShopDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/28 15:22
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DataStatisticsDetailViewModel : BaseViewModel() {
    private val mStatisticsRepo = ApiRepository.apiClient(DataStatisticsService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    var shopId = -1
    var shopName = ""

    val dateType: MutableLiveData<Int> = MutableLiveData(1)

    var startTime: MutableLiveData<Date> = MutableLiveData(DateTimeUtils.beforeDay(Date(), 7))
    var endTime: MutableLiveData<Date> = MutableLiveData(DateTimeUtils.beforeDay(Date(), 1))

    var startWeekTime: MutableLiveData<Date> =
        MutableLiveData(DateTimeUtils.beforeWeekFirstDay(Date()))
    var endWeekTime: MutableLiveData<Date> =
        MutableLiveData(DateTimeUtils.beforeWeekLastDay(Date()))

    var singleTime: MutableLiveData<Date> = MutableLiveData(Date())

    // 是否可提交
    val dateVal: MediatorLiveData<String> = MediatorLiveData("").apply {
        addSource(dateType) {
            value = calDateVal()
        }
        addSource(startTime) {
            value = calDateVal()
        }
        addSource(endTime) {
            value = calDateVal()
        }
        addSource(startWeekTime) {
            value = calDateVal()
        }
        addSource(endWeekTime) {
            value = calDateVal()
        }
        addSource(singleTime) {
            value = calDateVal()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun calDateVal(): String = when (dateType.value) {
        1 -> if (null != singleTime.value) {
            DateTimeUtils.formatDateTime(
                singleTime.value,
                "yyyy-MM-dd"
            )
        } else ""
        2 -> if (null != startWeekTime.value && null != endWeekTime.value) {
            "${
                DateTimeUtils.formatDateTime(
                    startWeekTime.value,
                    "MM-dd"
                )
            } 至 ${DateTimeUtils.formatDateTime(endWeekTime.value, "MM-dd")}"
        } else ""
        3 -> if (null != singleTime.value) {
            DateTimeUtils.formatDateTime(
                singleTime.value,
                "yyyy-MM"
            )
        } else ""
        4 -> if (null != singleTime.value) {
            DateTimeUtils.formatDateTime(
                singleTime.value,
                "yyyy年"
            )
        } else ""
        else -> ""
    }

    // 选择的店铺
    val selectDepartment: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 设备类型
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()

    // 选择的设备类型
    val selectDeviceCategory: MutableLiveData<CategoryEntity> by lazy {
        MutableLiveData()
    }

    // 店铺统计详情
    val statisticsShopDetail: MutableLiveData<DataStatisticsShopDetailEntity> by lazy {
        MutableLiveData()
    }

    /**
     * 请求设备类型
     */
    fun requestDeviceCategory() {
        launch({
            ApiRepository.dealApiResult(
                mCategoryRepo.category(1)
            )?.let {
                categoryList.postValue(it)
            }
        })
    }

    private fun commonParams() =
        hashMapOf<String, Any?>("dateType" to dateType.value!!).also { params ->
            when (dateType.value) {
                1 -> {
                    params["startTime"] = DateTimeUtils.formatDateTimeStartParam(singleTime.value)
                    params["endTime"] = DateTimeUtils.formatDateTimeEndParam(singleTime.value)
                }
                2 -> {
                    params["startTime"] =
                        DateTimeUtils.formatDateTimeStartParam(startWeekTime.value)
                    params["endTime"] = DateTimeUtils.formatDateTimeEndParam(endWeekTime.value)
                }
                3 -> {
                    params["startTime"] =
                        DateTimeUtils.formatDateTimeStartParam(
                            DateTimeUtils.getMonthFirst(singleTime.value)
                        )
                    params["endTime"] =
                        DateTimeUtils.formatDateTimeEndParam(DateTimeUtils.getMonthLast(singleTime.value))
                }
                4 -> {
                    params["startTime"] =
                        DateTimeUtils.formatDateTimeYearStartParam(singleTime.value)
                    params["endTime"] =
                        DateTimeUtils.formatDateTimeYearEndParam(singleTime.value)
                }
            }

            if (null != selectDepartment.value) {
                params["shopId"] = selectDepartment.value!!.id
            }

            if (null != selectDeviceCategory.value) {
                params["category"] = selectDeviceCategory.value!!.code
            }
        }

    fun requestShopDetailData() {
        launch({
            ApiRepository.dealApiResult(
                mStatisticsRepo.requestStatisticsShopDetail(
                    ApiRepository.createRequestBody(
                        commonParams()
                    )
                )
            )?.let {
                statisticsShopDetail.postValue(it)
            }
        })
    }
}