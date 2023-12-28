package com.yunshang.haile_manager_android.business.vm

import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.InvoiceReceiverEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity
import com.yunshang.haile_manager_android.data.entities.InvoiceWithdrawFeeEntity
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    val createInvoiceParams: MutableLiveData<InvoiceCreateRequest> =
        MutableLiveData(InvoiceCreateRequest())

    var selectFeeList: List<InvoiceWithdrawFeeEntity>? = null

    val isFold: MutableLiveData<Boolean> = MutableLiveData(true)

    val canIssuePaperInvoice: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val invoiceTitleList: MutableLiveData<MutableList<InvoiceTitleEntity>> by lazy {
        MutableLiveData()
    }

    val invoiceTitle: MutableLiveData<InvoiceTitleEntity> by lazy {
        MutableLiveData()
    }

    // 是否显示纸质发票
    val hasIssuePaperInvoice: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(invoiceTitle) {
            value = checkHasIssuePaperInvoice()
        }
        addSource(canIssuePaperInvoice) {
            value = checkHasIssuePaperInvoice()
        }
    }

    private fun checkHasIssuePaperInvoice() =
        true == canIssuePaperInvoice.value && true == invoiceTitle.value?.invoiceTitleType0Val

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

    // 收件人列表
    val invoiceReceiverList: MutableLiveData<MutableList<InvoiceReceiverEntity>> by lazy {
        MutableLiveData()
    }

    fun changeReceiver(receiver: InvoiceReceiverEntity) {
        if (receiver.email.isNullOrEmpty()) {
            createInvoiceParams.value?.receiverVal = receiver.receiver ?: ""
            createInvoiceParams.value?.smsPhoneVal = receiver.smsPhone ?: ""
            createInvoiceParams.value?.changeArea(
                receiver.provinceId,
                receiver.provinceName,
                receiver.cityId,
                receiver.cityName,
                receiver.districtId,
                receiver.districtName
            )
            createInvoiceParams.value?.addressVal = receiver.address ?: ""
        } else {
            createInvoiceParams.value?.smsPhone1Val = receiver.smsPhone ?: ""
            createInvoiceParams.value?.emailVal = receiver.email
        }
    }

    fun requestData(setLastTitle: Boolean = false) {
        launch({
            if (null == canIssuePaperInvoice.value) {
                ApiRepository.dealApiResult(
                    mCapitalRepo.requestSpecialInvoice()
                )?.let {
                    canIssuePaperInvoice.postValue(it)
                }
            }

            if (invoiceReceiverList.value.isNullOrEmpty()) {
                requestInvoiceReceiverList()
            }

            ApiRepository.dealApiResult(
                mCapitalRepo.requestInvoiceTitleList(
                    ApiRepository.createRequestBody(hashMapOf())
                )
            )?.let {
                invoiceTitleList.postValue(it)
                invoiceTitle.postValue((if (setLastTitle) it.firstOrNull()
                else it.find { item -> item.defaultVal } ?: it.firstOrNull())?.apply {
                    commonItemSelect = true
                })
            }
        })
    }

    private suspend fun requestInvoiceReceiverList() {
        ApiRepository.dealApiResult(
            mCapitalRepo.requestInvoiceReceiverList()
        )?.let {
            invoiceReceiverList.postValue(it)
            // 设置默认
            it.firstOrNull { item -> !item.email.isNullOrEmpty() }?.let { first ->
                changeReceiver(first.apply {
                    commonItemSelect = true
                })
            }
            it.firstOrNull { item -> item.email.isNullOrEmpty() }?.let { first ->
                changeReceiver(first.apply {
                    commonItemSelect = true
                })
            }
        }
    }

    fun deleteInvoiceTitle(id: Int?, callback: () -> Unit) {
        if (!id.hasVal()) return
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.deleteInvoiceReceiver(id!!)
            )
            requestInvoiceReceiverList()
            withContext(Dispatchers.Main) {
                callback()
            }
        })
    }

    fun submit(v: View) {
        if (null == createInvoiceParams.value) return

        val params = createInvoiceParams.value!!.let { params ->
            if (1 == params.type) {
                params.copy(
                    receiver = null,
                    smsPhone = params.smsPhone1Val,
                    provinceId = null,
                    cityId = null,
                    districtId = null,
                    address = null,
                )
            } else {
                params.copy(
                    email = null
                )
            }
        }
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.issueInvoice(
                    ApiRepository.createRequestBody(GsonUtils.any2Json(params))
                )
            )

            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.submit_success)
            }
            LiveDataBus.post(BusEvents.INVOICE_FEE_LIST_STATUS, true)
            jump.postValue(0)
        })
    }

    data class InvoiceCreateRequest(
        var invoiceTemplateId: Int? = null,
        var chargeType: Int? = 1,
        var type: Int? = null,
        var businessNos: List<String>? = null,
        var `receiver`: String? = null,
        var smsPhone: String? = null,
        var provinceId: Int? = null,
        var cityId: Int? = null,
        var districtId: Int? = null,
        var address: String? = null,
        var email: String? = null
    ) : BaseObservable() {

        fun changeInvoiceTemplateId(templateId: Int?) {
            invoiceTemplateId = templateId
            notifyPropertyChanged(BR.canSubmit)
        }

        /**
         * @param type 发票类型<1-普通电子发票2-增值税发票>
         */
        @get:Bindable
        var typeVal: Int?
            get() = type
            set(value) {
                type = value
                notifyPropertyChanged(BR.typeVal)
                notifyPropertyChanged(BR.canSubmit)
            }

        fun checkType(type: Int) {
            typeVal = type
        }

        fun changeBusinessNos(businessNos: List<String>?) {
            this.businessNos = businessNos
            notifyPropertyChanged(BR.canSubmit)
        }

        fun clearReceiver() {
            receiverVal = ""
            smsPhoneVal = ""
            smsPhone1Val = ""
            changeArea(null, null, null, null, null, null)
            addressVal = ""
            emailVal = ""
        }

        @get:Bindable
        var receiverVal: String
            get() = receiver ?: ""
            set(value) {
                receiver = value
                notifyPropertyChanged(BR.receiverVal)
                notifyPropertyChanged(BR.canSubmit)
            }

        @Transient
        @get:Bindable
        var smsPhone1Val: String? = null
            get() = (field ?: smsPhone) ?: ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.smsPhone1Val)
                notifyPropertyChanged(BR.canSubmit)
            }

        @get:Bindable
        var smsPhoneVal: String
            get() = smsPhone ?: ""
            set(value) {
                smsPhone = value
                notifyPropertyChanged(BR.smsPhoneVal)
                notifyPropertyChanged(BR.canSubmit)
            }

        fun changeArea(
            provinceId: Int?,
            province: String?,
            cityId: Int?,
            city: String?,
            districtId: Int?,
            district: String?
        ) {
            this.provinceId = provinceId
            this.cityId = cityId
            this.districtId = districtId
            areaVal = (province ?: "") + (city ?: "") + (district ?: "")
            notifyPropertyChanged(BR.canSubmit)
        }

        @Transient
        @get:Bindable
        var areaVal: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.areaVal)
            }

        @get:Bindable
        var addressVal: String
            get() = address ?: ""
            set(value) {
                address = value
                notifyPropertyChanged(BR.addressVal)
                notifyPropertyChanged(BR.canSubmit)
            }

        @get:Bindable
        var emailVal: String
            get() = email ?: ""
            set(value) {
                email = value
                notifyPropertyChanged(BR.emailVal)
                notifyPropertyChanged(BR.canSubmit)
            }

        @get:Bindable
        val canSubmit: Boolean
            get() = invoiceTemplateId.hasVal() && type.hasVal() && !businessNos.isNullOrEmpty() && (if (1 == type) {
                !smsPhone1Val.isNullOrEmpty() && smsPhone1Val!!.length == 11 && !email.isNullOrEmpty() && email!!.length >= 6 && email!!.contains(
                    "."
                ) && email!!.contains("@")
            } else {
                !receiver.isNullOrEmpty() && !smsPhone.isNullOrEmpty() && smsPhone!!.length == 11 && provinceId.hasVal() && cityId.hasVal() && districtId.hasVal() && !address.isNullOrEmpty()
            })

    }
}