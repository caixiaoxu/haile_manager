package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.arguments.CalendarEntity
import com.yunshang.haile_manager_android.data.entities.IncomeCalendarEntity
import com.yunshang.haile_manager_android.data.entities.ProfitStatisticsEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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
class IncomeCalendarViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var profitType: Int = 3

    var profitSearchId: Int = -1

    var deviceName: String = ""

    // 1:个人收益；2:商家收益
    var profitIncomeType: Int = 1

    //选择的日期
    val selectDay: MutableLiveData<Date> = MutableLiveData(Date())

    //选择的月份
    val selectMonth: MutableLiveData<Date> = MutableLiveData(DateTimeUtils.curMonthFirst)
    val selectMonthVal: LiveData<String> = selectMonth.map {
        DateTimeUtils.formatDateTime(it, "yyyy年MM月")
    }
    val canAddDay: LiveData<Boolean> = selectMonth.map {
        !DateTimeUtils.isSameMonth(it, Date())
    }

    val totalIncome: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val totalIncomeForDay: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val calendarIncome: MutableLiveData<MutableList<CalendarEntity>> by lazy {
        MutableLiveData()
    }

    var transactionType: Int? = null

    /**
     * 共有参数
     */
    private fun getCommonParams(isMonth: Boolean = true): HashMap<String, Any?>? =
        hashMapOf<String, Any?>(
            "dateType" to 1,//日期统计类型 ，1：天；2：月；3：年
            "profitType" to profitType, //收益类型 1:店铺；2：设备；3:收入明细
            "profitIncomeType" to profitIncomeType
        ).also { params ->
            if (3 != profitType) {
                if (-1 == profitSearchId) {
                    Timber.e("缺少profitSearchId参数")
                    return null
                }

                params["profitSearchId"] = profitSearchId
            }
            if (isMonth) {
                params["startTime"] =
                    DateTimeUtils.formatDateTimeStartParam(DateTimeUtils.getMonthFirst(selectMonth.value!!))
                params["endTime"] =
                    DateTimeUtils.formatDateTimeEndParam(DateTimeUtils.getMonthLast(selectMonth.value!!))
            } else {
                params["startTime"] =
                    DateTimeUtils.formatDateTimeStartParam(selectDay.value!!)
                params["endTime"] =
                    DateTimeUtils.formatDateTimeEndParam(selectDay.value!!)
            }
        }

    /**
     * 初始化日历列表
     */
    private fun initCalendarList() =
        mutableListOf<CalendarEntity>().also { list ->
            selectMonth.value?.let { date ->
                Calendar.getInstance().run {
                    time = date
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)

                    val weekNum = get(Calendar.DAY_OF_WEEK)
                    // 1号前的空白格子
                    var before = weekNum - Calendar.MONDAY
                    if (Calendar.SUNDAY == weekNum) {
                        before = 6
                    }
                    if (before > 0) {
                        for (i in 1..before) {
                            list.add(CalendarEntity(-1))
                        }
                    }
                    // 当月的天数
                    val dayNum = getActualMaximum(Calendar.DAY_OF_MONTH)
                    for (i in 1..dayNum) {
                        set(Calendar.DAY_OF_MONTH, i)
                        list.add(
                            CalendarEntity(
                                0,
                                DateTimeUtils.formatDateTime(time, "yyyy-MM-dd HH:mm:ss")
                            )
                        )
                    }
                    // 当月最后一天的空白格子
                    val temp = (before + dayNum) % 7
                    if (temp > 0) {
                        val after = 7 - temp
                        for (i in 1..after) {
                            list.add(CalendarEntity(-1))
                        }
                    }
                }
            }
        }

    /**
     * 查询当前选择日期的总收益
     */
    fun requestTotalAmount(isMonth: Boolean) {
        val params = getCommonParams(isMonth) ?: return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.totalIncome(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )?.let {
                if (isMonth) {
                    totalIncome.postValue(StringUtils.formatNumberStrOfStr(it))
                } else {
                    totalIncomeForDay.postValue(StringUtils.formatNumberStrOfStr(it))
                }
            }
        })
    }

    /**
     * 收益列表
     */
    fun requestIncomeByDate() {
        val params = getCommonParams() ?: return
        launch({
            val calendarList = initCalendarList()
            ApiRepository.dealApiResult(
                mCapitalRepo.incomeByDate(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )?.let {
                val map = hashMapOf<String, IncomeCalendarEntity>()
                it.forEach { income ->
                    map[income.date] = income
                }

                // 赋值
                calendarList.forEach { calendar ->
                    if (calendar.type != -1 && !calendar.day.isNullOrEmpty())
                        calendar.initIncome(map[calendar.day])
                }
                calendarIncome.postValue(calendarList)
            }
        })
    }

    /**
     * 日期操作
     */
    fun dateOperate(view: View, month: Int) {
        selectMonth.value?.let { date ->
            Calendar.getInstance().run {
                time = date
                add(Calendar.MONTH, month)
                selectMonth.postValue(time)
            }
        }
    }

    /**
     * 请求当天收益列表
     */
    fun requestIncomeListForDay(
        page: Int, pageSize: Int,
        callBack: (responseList: ResponseList<out ProfitStatisticsEntity>?) -> Unit
    ) {
        val params = getCommonParams(false) ?: return
        params["page"] = page
        params["pageSize"] = pageSize
        params["transactionType"] = transactionType
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.requestProfitStatisticsList(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    callBack.invoke(it)
                }
            }
        })
    }
}