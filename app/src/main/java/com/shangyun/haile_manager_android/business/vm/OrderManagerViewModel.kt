package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.OrderService
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.OrderListEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.rule.IndicatorEntity
import com.shangyun.haile_manager_android.utils.DateTimeUtils
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
        MutableLiveData()
    }

    val endTime: MutableLiveData<Date> by lazy {
        MutableLiveData()
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
                    "yyyy-MM-dd",
                    startTime.value
                )
            } else {
                "${
                    DateTimeUtils.formatDateTime(
                        "yyyy-MM-dd",
                        startTime.value
                    )
                } 至 ${DateTimeUtils.formatDateTime("yyyy-MM-dd", endTime.value)}"
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
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                result.invoke(null)
            }
        }, { Timber.d("请求结束") }, 1 == page)
    }
}