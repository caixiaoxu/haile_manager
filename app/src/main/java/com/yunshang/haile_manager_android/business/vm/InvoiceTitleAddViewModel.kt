package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
class InvoiceTitleAddViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    val invoiceTitleAddParams: MutableLiveData<InvoiceTitleEntity> =
        MutableLiveData(InvoiceTitleEntity())

    fun save(v: View) {
        if (null == invoiceTitleAddParams.value) return

        if (0 == invoiceTitleAddParams.value!!.isPersonal && invoiceTitleAddParams.value!!.taxNo!!.length < 15) {
            SToast.showToast(v.context, "请填写正确的公司税号")
            return
        }

        launch({
            val params = if (1 == invoiceTitleAddParams.value?.isPersonal) {
                invoiceTitleAddParams.value?.copy(
                    taxNo = null,
                    bankName = null,
                    bankAccount = null,
                    phone = null,
                    address = null,
                )
            } else {
                invoiceTitleAddParams.value
            }

            val body = ApiRepository.createRequestBody(
                GsonUtils.any2Json(params)
            )
            ApiRepository.dealApiResult(
                if (invoiceTitleAddParams.value?.id.hasVal()) {
                    mCapitalRepo.updateInvoiceTitle(body)
                } else {
                    mCapitalRepo.createInvoiceTitle(body)
                }
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.submit_success)
            }
            if (invoiceTitleAddParams.value?.id.hasVal()) {
                LiveDataBus.post(BusEvents.INVOICE_TITLE_LIST_STATUS, true)
                LiveDataBus.post(BusEvents.INVOICE_TITLE_DETAILS_STATUS, true)
            } else {
                LiveDataBus.post(BusEvents.INVOICE_TITLE_ADD_STATUS, true)
            }
            jump.postValue(0)
        })
    }
}