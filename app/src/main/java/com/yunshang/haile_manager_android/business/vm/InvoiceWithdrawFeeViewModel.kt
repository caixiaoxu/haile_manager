package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceUserEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/18 10:36
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class InvoiceWithdrawFeeViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    // 时间区间
    val startTime: MutableLiveData<Date> = MutableLiveData(DateTimeUtils.curMonthFirst)
    val endTime: MutableLiveData<Date> = MutableLiveData(Date())

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
                    "MM-dd"
                )
            } else {
                val formatStr = if (DateTimeUtils.isSameYear(
                        startTime.value!!,
                        endTime.value!!
                    )
                ) "MM-dd" else "yyyy-MM-dd"

                "${
                    DateTimeUtils.formatDateTime(startTime.value, formatStr)
                } 至 ${DateTimeUtils.formatDateTime(endTime.value, formatStr)}"
            }
        } else StringUtils.getString(R.string.cash_out_time)

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<Int> = MutableLiveData(0)

    val selectBatchNumVal: LiveData<String> = selectBatchNum.map {
        if (0 == it) "" else "${StringUtils.getString(R.string.selected)} $it"
    }

    fun refreshSelectBatchNum(list: MutableList<InvoiceWithdrawFeeEntity>) {
        selectBatchNum.value = list.count { item -> item.selected }
        isAll.value =
            if (list.isNotEmpty()) list.all { item -> 2 == item.invoiceStatus || item.selected } else false
    }

    val invoiceUserList: MutableLiveData<List<InvoiceUserEntity>> by lazy {
        MutableLiveData()
    }

    val selectInvoiceUserList: MutableLiveData<List<InvoiceUserEntity>> by lazy {
        MutableLiveData()
    }

    val selectInvoiceUserVal: LiveData<String> = selectInvoiceUserList.map {
        if (it.isNullOrEmpty()) ""
        else "已选${it.filter {item-> -1 != item.id }.size}个"
    }

    fun requestInvoiceWithdrawFeeList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out InvoiceWithdrawFeeEntity>?) -> Unit
    ) {
        launch({
            if (invoiceUserList.value.isNullOrEmpty()) {
                ApiRepository.dealApiResult(
                    mCapitalRepo.requestInvoiceUserList()
                )?.let {
                    invoiceUserList.postValue(it.apply {
                        add(0, InvoiceUserEntity(-1, StringUtils.getString(R.string.all)))

                        forEach {item->
                            item.commonItemSelect = true
                        }
                    })
                }
            }

            val result = ApiRepository.dealApiResult(
                mCapitalRepo.requestInvoiceCashOutList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                            "applyStartDate" to DateTimeUtils.formatDateTimeStartParam(startTime.value),
                            "applyEndDate" to DateTimeUtils.formatDateTimeEndParam(endTime.value),
                            "accountIds" to selectInvoiceUserList.value?.mapNotNull { it.id }
                                ?.let { idList ->
                                    if (null != idList.find { -1 == it }) null else idList
                                },
                            "invoiceStatus" to -1
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack(result)
            }
        }, {
            // 自己定义的错误显示报错提示
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                if (it is CommonCustomException) {
                    it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(msg = "网络开小差~")
                }
                callBack(null)
            }
        })
    }
}