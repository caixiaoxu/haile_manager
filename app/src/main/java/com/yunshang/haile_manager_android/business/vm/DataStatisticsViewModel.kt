package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
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
class DataStatisticsViewModel : BaseViewModel() {
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

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
        1 -> if (null != startTime.value && null != endTime.value) {
            if (DateTimeUtils.isSameDay(startTime.value, endTime.value)) {
                DateTimeUtils.formatDateTime(startTime.value, "yyyy-MM-dd")
            } else {
                "${DateTimeUtils.formatDateTime(startTime.value, "MM-dd")} 至 ${
                    DateTimeUtils.formatDateTime(
                        endTime.value,
                        "MM-dd"
                    )
                }"
            }
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
}