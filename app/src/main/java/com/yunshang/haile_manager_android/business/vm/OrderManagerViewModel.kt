package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.OrderService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.IndicatorEntity
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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
class OrderManagerViewModel : BaseViewModel() {
    private val mOrderRepo = ApiRepository.apiClient(OrderService::class.java)

    // 设备数量
    val mOrderCountStr: MutableLiveData<String> = MutableLiveData()

    // 选择的店铺
    val selectDepartment: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 时间区间
    val startTime: MutableLiveData<Date> by lazy {
        MutableLiveData(DateTimeUtils.curMonthFirst)
    }

    val endTime: MutableLiveData<Date> by lazy {
        MutableLiveData(Date())
    }

    // 是否可提交
    val timeStr: MediatorLiveData<String> =
        MediatorLiveData<String>(StringUtils.getString(R.string.order_time)).apply {
            addSource(startTime) {
                value = timeFormat()
            }
            addSource(endTime) {
                value = timeFormat()
            }
        }

    private fun timeFormat() =
        if (null != startTime.value && null != endTime.value) {
            if (DateTimeUtils.isSameDay(startTime.value!!, endTime.value!!)) {
                DateTimeUtils.formatDateTime(
                    startTime.value,
                    "yyyy-MM-dd"
                )
            } else {
                "${
                    DateTimeUtils.formatDateTime(
                        startTime.value,
                        "yyyy-MM-dd"
                    )
                } 至 ${DateTimeUtils.formatDateTime(endTime.value, "yyyy-MM-dd")}"
            }
        } else StringUtils.getString(R.string.order_time)


    // 状态的工作状态
    val curOrderStatus: MutableLiveData<String> = MutableLiveData("")

    val orderStatus: MutableLiveData<List<IndicatorEntity<String>>> = MutableLiveData(
        arrayListOf(
            IndicatorEntity("全部", 0, ""),
            IndicatorEntity("待支付", 0, "100"),
            IndicatorEntity("已支付", 0, "500"),
            IndicatorEntity("已完成", 0, "1000"),
            IndicatorEntity("已退款", 0, "2099"),
            IndicatorEntity("支付超时", 0, "401"),
        )
    )

    fun requestOrderList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<OrderListEntity>?) -> Unit

    ) {
        launch({
            val params: HashMap<String, Any> = hashMapOf(
                "page" to page,
                "pageSize" to pageSize,
                "orderStatus" to (curOrderStatus.value ?: ""),
            )
            // 店铺
            selectDepartment.value?.let {
                params["shopId"] = it.id
            }
            // 时间
            startTime.value?.let {
                params["startTime"] = DateTimeUtils.formatDateTime(it,"yyyy-MM-dd") + " 00:00:00"
            }
            endTime.value?.let {
                params["endTime"] = DateTimeUtils.formatDateTime(it,"yyyy-MM-dd") + " 23:59:59"
            }

            val listWrapper = ApiRepository.dealApiResult(
                mOrderRepo.requestOrderList(params)
            )
            listWrapper?.let {
                mOrderCountStr.postValue(
                    StringUtils.getString(R.string.order_num_hint, it.total),
                )
                withContext(Dispatchers.Main) {
                    result.invoke(it)
                }
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }
}