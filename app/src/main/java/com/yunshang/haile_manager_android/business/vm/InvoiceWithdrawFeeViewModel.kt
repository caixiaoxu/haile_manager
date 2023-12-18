package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
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
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class InvoiceWithdrawFeeViewModel: BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<Int> = MutableLiveData(0)

    val selectBatchNumVal: LiveData<String> = selectBatchNum.map {
        if (0 == it) "" else "${StringUtils.getString(R.string.selected)} $it"
    }

    fun refreshSelectBatchNum(list: MutableList<InvoiceWithdrawFeeEntity>) {
        selectBatchNum.value = list.count { item -> item.selected }
        isAll.value =
            if (list.isNotEmpty()) list.all { item -> 2 != item.invoiceStatus || item.selected } else false
    }

    fun requestInvoiceWithdrawFeeList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out InvoiceWithdrawFeeEntity>?) -> Unit
    ) {
        launch({
            val result = ApiRepository.dealApiResult(
                mCapitalRepo.requestInvoiceCashOutList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
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