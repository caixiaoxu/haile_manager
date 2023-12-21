package com.yunshang.haile_manager_android.business.vm

import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/20 10:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class IssueInvoiceViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var selectFeeList: List<InvoiceWithdrawFeeEntity>? = null

    val isFold: MutableLiveData<Boolean> = MutableLiveData(true)

    val invoiceTitle: MutableLiveData<InvoiceTitleEntity> by lazy {
        MutableLiveData()
    }

    fun feeTotal() =
        (selectFeeList?.fold(0.0) { sum, element -> sum + element.fee } ?: 0.0).let { total ->
            StringUtils.formatMultiStyleStr(
                "${total}元", arrayOf(
                    ForegroundColorSpan(
                        ResourcesCompat.getColor(
                            Constants.APP_CONTEXT.resources,
                            R.color.colorPrimary,
                            null
                        )
                    ),
                ), 0, total.toString().length
            )
        }

    val invoiceTitleList: MutableLiveData<MutableList<InvoiceTitleEntity>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.requestInvoiceTitleList(
                    ApiRepository.createRequestBody(hashMapOf())
                )
            )?.let {
                invoiceTitleList.postValue(it)
                invoiceTitle.postValue(it.find { item -> item.defaultVal } ?: it.firstOrNull())
            }
        })
    }
}