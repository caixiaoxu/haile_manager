package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/19 10:50
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class InvoiceTitleEntity(
    var isPersonal: Int? = 0,
    var title: String? = null,
    var taxNo: String? = null,
    var bankName: String? = null,
    var bankAccount: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var isDefault: Int? = null,
    val id: Int? = null,
    val type: Int? = null,
//
//    val business: String? = null,
//    val context: String? = null,
//    val createTime: String? = null,
//    val creatorId: Int? = null,
//    val deleteFlag: Int? = null,
//    val email: String? = null,
//    val lastEditor: Int? = null,
//    val name: String? = null,
//    val smsPhone: String? = null,
//    val taxpayer: String? = null,
//    val updateTime: String? = null,
//    val userId: Int? = null,
//    val version: Int? = null
) : BaseObservable() {

    @get:Bindable
    var invoiceTitleType0Val: Boolean
        get() = 0 == isPersonal
        set(value) {
            isPersonal = if (value) 0 else 1
            notifyPropertyChanged(BR.invoiceTitleType0Val)
            notifyPropertyChanged(BR.canSubmit)
        }

    @get:Bindable
    var invoiceTitleType1Val: Boolean
        get() = 1 == isPersonal
        set(value) {
            isPersonal = if (value) 1 else 0
            notifyPropertyChanged(BR.invoiceTitleType1Val)
            notifyPropertyChanged(BR.canSubmit)
        }

    fun isPersonalVal(): String =
        StringUtils.getString(if (1 == isPersonal) R.string.invoice_title_type1 else R.string.invoice_title_type0)
    fun typeVal(): String =
        StringUtils.getString(if (2 == type) R.string.invoice_type2 else R.string.invoice_type1)

    @get:Bindable
    var titleVal: String
        get() = title ?: ""
        set(value) {
            title = value
            notifyPropertyChanged(BR.titleVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    fun clearTitleVal() {
        titleVal = ""
    }

    @get:Bindable
    var taxNoVal: String
        get() = taxNo ?: ""
        set(value) {
            taxNo = value
            notifyPropertyChanged(BR.taxNoVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    fun clearTaxNoVal() {
        taxNoVal = ""
    }

    @get:Bindable
    var bankNameVal: String
        get() = bankName ?: ""
        set(value) {
            bankName = value
            notifyPropertyChanged(BR.bankNameVal)
            notifyPropertyChanged(BR.bankNameNumVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    @get:Bindable
    val bankNameNumVal: String
        get() = "${if (bankName.isNullOrEmpty()) 0 else bankName!!.length}/50"

    @get:Bindable
    var bankAccountVal: String
        get() = bankAccount ?: ""
        set(value) {
            bankAccount = value
            notifyPropertyChanged(BR.bankAccountVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    fun clearBankAccountVal() {
        bankAccountVal = ""
    }

    @get:Bindable
    var phoneVal: String
        get() = phone ?: ""
        set(value) {
            phone = value
            notifyPropertyChanged(BR.phoneVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    fun clearPhoneVal() {
        phoneVal = ""
    }

    @get:Bindable
    var addressVal: String
        get() = address ?: ""
        set(value) {
            address = value
            notifyPropertyChanged(BR.addressVal)
            notifyPropertyChanged(BR.addressValNumVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    @get:Bindable
    val addressValNumVal: String
        get() = "${if (address.isNullOrEmpty()) 0 else address!!.length}/50"


    @get:Bindable
    var defaultVal: Boolean
        get() = 1 == isDefault
        set(value) {
            isDefault = if (value) 1 else 0
            notifyPropertyChanged(BR.defaultVal)
            notifyPropertyChanged(BR.canSubmit)
        }

    @get:Bindable
    val canSubmit: Boolean
        get() = null != isPersonal && !title.isNullOrEmpty() && title!!.length >= 15 && (if (1 == isPersonal) !taxNo.isNullOrEmpty() else true)
}