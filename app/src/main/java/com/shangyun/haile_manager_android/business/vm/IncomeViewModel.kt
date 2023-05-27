package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.CapitalService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import com.shangyun.haile_manager_android.utils.StringUtils
import timber.log.Timber
import java.util.Calendar
import java.util.Date

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
class IncomeViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var profitType: Int = 3

    var profitSearchId: Int = -1

    val selectDay: MutableLiveData<Date> = MutableLiveData(Date())
    val selectDayVal: LiveData<String> = selectDay.map {
        DateTimeUtils.formatDateTime(it, "yyyy年MM月")
    }
    val canAddDay: LiveData<Boolean> = selectDay.map {
        !DateTimeUtils.isSameMonth(it, Date())
    }

    val totalIncome: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    
    private fun getCommonParams(): HashMap<String, Any>? = hashMapOf<String, Any>(
        "profitType" to profitType, //收益类型 1:店铺；2：设备；3:收入明细
    ).also { params ->
        if (3 != profitType) {
            if (-1 == profitSearchId) {
                Timber.e("缺少profitSearchId参数")
                return null
            }

            params["profitSearchId"] = profitSearchId
        }
        params["startTime"] = DateTimeUtils.formatDateTimeStartParam(selectDay.value!!)
        params["endTime"] = DateTimeUtils.formatDateTimeEndParam(selectDay.value!!)
    }

    fun requestDayOfMonth() {
        selectDay.value?.let { date ->
            Calendar.getInstance().run {
                time = date
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)

                val weekNum = get(Calendar.DAY_OF_WEEK)
                var before = weekNum - Calendar.MONDAY
                if (Calendar.SUNDAY == weekNum) {
                    before = 6
                }
                for (i in  1..6){
                    
                }
                
                val dayNum = getActualMaximum(Calendar.DAY_OF_MONTH)
                val temp = (before + dayNum) % 7
                var after = 0
                if (temp > 0) {
                    after = 7 - temp
                }
            }
        }
    }

    /**
     * 查询当前选择日期的总收益
     */
    fun requestTotalForDay() {
        val params = getCommonParams() ?: return
        params["dateType"] = 1 //日期统计类型 ，1：天；2：月；3：年
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.totalIncome(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )?.let {
                totalIncome.postValue(StringUtils.formatNumberStrOfStr(it))
            }
        })
    }

    /**
     * 收益列表
     */
    fun requestIncomeByDate() {
        val params = getCommonParams() ?: return
        params["dateType"] = 1 //日期统计类型 ，1：天；2：月；3：年
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.incomeByDate(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )?.let {
            }
        })
    }

    /**
     * 日期操作
     */
    fun dateOperate(view: View, month: Int) {
        selectDay.value?.let { date ->
            Calendar.getInstance().run {
                time = date
                add(Calendar.MONTH, month)
                selectDay.postValue(time)
            }
        }
    }
}