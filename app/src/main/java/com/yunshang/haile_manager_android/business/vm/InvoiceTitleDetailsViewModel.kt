package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/19 14:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class InvoiceTitleDetailsViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var invoiceTitleId: Int? = null

    val invoiceTitle: MutableLiveData<InvoiceTitleEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (!invoiceTitleId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.requestInvoiceTitleDetails(invoiceTitleId!!)
            )?.let {
                invoiceTitle.postValue(it)
            }
        })
    }

    fun deleteInvoiceTitle() {
        if (!invoiceTitleId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.deleteInvoiceTitle(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to invoiceTitleId
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.INVOICE_TITLE_LIST_STATUS, true)
            jump.postValue(0)
        })
    }
}