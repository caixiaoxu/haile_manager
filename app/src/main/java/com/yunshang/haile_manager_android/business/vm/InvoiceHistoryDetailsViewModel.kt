package com.yunshang.haile_manager_android.business.vm

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.InvoiceUserEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.data.entities.IssueInvoiceDetailsEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
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
class InvoiceHistoryDetailsViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    var invoiceId: Int? = null

    val invoiceDetails: MutableLiveData<IssueInvoiceDetailsEntity> by lazy {
        MutableLiveData()
    }

    val invoiceAmount: LiveData<SpannableString> = invoiceDetails.map {
        (it.amount)?.let { total ->
            com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
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
        } ?: SpannableString("")
    }

    fun requestData() {
        if (!invoiceId.hasVal()) return
        launch({
            ApiRepository.dealApiResult(mCapitalRepo.requestInvoiceDetails(invoiceId!!))?.let {
                invoiceDetails.postValue(it)
            }
        })
    }
}