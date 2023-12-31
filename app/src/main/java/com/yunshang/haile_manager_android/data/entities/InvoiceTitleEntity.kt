package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.ICommonNewBottomItemEntity

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
    val type: Int? = 1,
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
) : ICommonNewBottomItemEntity() {

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

    fun isPersonalVal1(): String =
        StringUtils.getString(if (1 == isPersonal) R.string.invoice_title_type1 else R.string.invoice_title_type2)

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

    @Transient
    @get:Bindable
    var taxNoVal: String? = null
        get() = if (null == field) taxNo?.chunked(4)?.joinToString(" ") ?: "" else field
        set(value) {
            val origin = value!!.trim().replace(" ", "")
            field = origin.chunked(4).joinToString(" ")
            taxNo = value.trim().replace(" ", "")
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

    @Transient
    @get:Bindable
    var bankAccountVal: String? = null
        get() = if (null == field) bankAccount?.chunked(4)?.joinToString(" ") ?: "" else field
        set(value) {
            val origin = value!!.trim().replace(" ", "")
            field = origin.chunked(4).joinToString(" ")
            bankAccount = origin
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
        get() = null != isPersonal && !title.isNullOrEmpty() && (if (0 == isPersonal) !taxNo.isNullOrEmpty() && !bankName.isNullOrEmpty() && !bankAccount.isNullOrEmpty() && !phone.isNullOrEmpty() && !address.isNullOrEmpty() else true)
}