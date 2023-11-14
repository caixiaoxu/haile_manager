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
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.DeviceIndicatorEntity
import com.yunshang.haile_manager_android.data.rule.IndicatorEntity
import com.yunshang.haile_manager_android.utils.DateTimeUtils
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
class OrderManagerViewModel : BaseViewModel() {
    private val mOrderRepo = ApiRepository.apiClient(OrderService::class.java)

    // 搜索内容
    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 设备数量
    val mOrderCountStr: MutableLiveData<String> = MutableLiveData()

    // 选择的店铺
    val selectDepartments: MutableLiveData<MutableList<SearchSelectParam>?> by lazy {
        MutableLiveData()
    }

    // 选择的店铺点位
    val selectDepartmentPositions: MutableLiveData<MutableList<ShopAndPositionSelectEntity>?> by lazy {
        MutableLiveData()
    }

    // 时间区间
    val startTime: MutableLiveData<Date> by lazy {
        MutableLiveData(DateTimeUtils.curMonthFirst)
    }

    val endTime: MutableLiveData<Date> by lazy {
        MutableLiveData(Date())
    }

    // 时间区间
    val timeStr: MediatorLiveData<String> =
        MediatorLiveData(StringUtils.getString(R.string.order_time)).apply {
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
            IndicatorEntity("待支付", 0, "1"),
            IndicatorEntity("进行中", 0, "2"),
            IndicatorEntity("已完成", 0, "3"),
            IndicatorEntity("已退款", 0, "4"),
        )
    )

    val selectErrorStatus: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }
    val errorStatus: List<DeviceIndicatorEntity<Int>> =
        arrayListOf(
            DeviceIndicatorEntity("故障订单", MutableLiveData(0), 431),
            DeviceIndicatorEntity("预约", MutableLiveData(0), 300),
            DeviceIndicatorEntity("超时关闭", MutableLiveData(0), 401),
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
                "newOrderStatus" to (curOrderStatus.value ?: ""),
                "searchType" to if (searchKey.value.isNullOrEmpty()) 1 else 2,
                "searchStr" to (searchKey.value?.trim() ?: ""),
            )

            // 店铺
            params["shopIds"] = selectDepartments.value?.map { it.id }?.joinToString(",") ?: ""
            // 点位
            params["positionIds"] = selectDepartmentPositions.value?.flatMap { item ->
                item.positionList?.mapNotNull { pos -> pos.id } ?: listOf()
            }?.joinToString(",") ?: ""
            // 时间
            startTime.value?.let {
                params["startTime"] = DateTimeUtils.formatDateTime(it, "yyyy-MM-dd") + " 00:00:00"
            }
            endTime.value?.let {
                params["endTime"] = DateTimeUtils.formatDateTime(it, "yyyy-MM-dd") + " 23:59:59"
            }

            if (1 == page && curOrderStatus.value.isNullOrEmpty()){
                ApiRepository.dealApiResult(
                    mOrderRepo.requestSummaryCount(params)
                )?.let {
                    errorStatus[0].num.postValue(it.faultCount)
                    errorStatus[1].num.postValue(it.reserveCount)
                    errorStatus[2].num.postValue(it.closeCount)
                }
            }

            if (curOrderStatus.value.isNullOrEmpty()){
                // 特殊筛选数量
                selectErrorStatus.value?.let {
                    if (300 == it) {
                        params["orderType"] = 300
                    } else {
                        params["orderStatus"] = it
                    }
                }
            }

            val listWrapper = ApiRepository.dealApiResult(
                mOrderRepo.requestOrderList(params)
            )
            mOrderCountStr.postValue(
                StringUtils.getString(R.string.order_num_hint, listWrapper?.total ?: 0),
            )
            withContext(Dispatchers.Main) {
                result.invoke(listWrapper)
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