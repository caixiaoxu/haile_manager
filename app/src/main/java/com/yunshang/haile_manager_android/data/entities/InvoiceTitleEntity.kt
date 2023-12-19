package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

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

    val business: String? = null,
    val context: String? = null,
    val createTime: String? = null,
    val creatorId: Int? = null,
    val deleteFlag: Int? = null,
    val email: String? = null,
    val id: Int? = null,
    val lastEditor: Int? = null,
    val name: String? = null,
    val smsPhone: String? = null,
    val taxpayer: String? = null,
    val type: Int? = null,
    val updateTime: String? = null,
    val userId: Int? = null,
    val version: Int? = null
) : BaseObservable() {

    @get:Bindable
    var invoiceTitleType0Val: Boolean
        get() = 0 == isPersonal
        set(value) {
            isPersonal = if (value) 0 else 1
            notifyPropertyChanged(BR.invoiceTitleType0Val)
        }

    @get:Bindable
    var invoiceTitleType1Val: Boolean
        get() = 1 == isPersonal
        set(value) {
            isPersonal = if (value) 1 else 0
            notifyPropertyChanged(BR.invoiceTitleType1Val)
        }

    @get:Bindable
    var titleVal: String
        get() = title ?: ""
        set(value) {
            title = value
            notifyPropertyChanged(BR.titleVal)
        }

    @get:Bindable
    var taxNoVal: String
        get() = taxNo ?: ""
        set(value) {
            taxNo = value
            notifyPropertyChanged(BR.taxNoVal)
        }

    @get:Bindable
    var bankNameVal: String
        get() = bankName ?: ""
        set(value) {
            bankName = value
            notifyPropertyChanged(BR.bankNameVal)
            notifyPropertyChanged(BR.bankNameNumVal)
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
        }

    @get:Bindable
    var phoneVal: String
        get() = phone ?: ""
        set(value) {
            phone = value
            notifyPropertyChanged(BR.phoneVal)
        }

    @get:Bindable
    var addressVal: String
        get() = address ?: ""
        set(value) {
            address = value
            notifyPropertyChanged(BR.addressVal)
        }

    @get:Bindable
    var isDefaultVal: Boolean
        get() = 1 == isDefault
        set(value) {
            isDefault = if (value) 1 else 0
            notifyPropertyChanged(BR.isDefaultVal)
        }
}