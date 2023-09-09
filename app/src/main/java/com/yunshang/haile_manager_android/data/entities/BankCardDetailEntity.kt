package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/6 18:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class BankCardDetailEntity(
    var address: String? = null,
    var authCode: String? = null,
    var bankAccountName: String? = null,
    var bankAccountNo: String? = null,
    var bankCardImage: String? = null,
    var bankCityId: Int? = null,
    var bankCode: String? = null,
    var bankDistrictId: Int? = null,
    var bankMobileNo: String? = null,
    var bankName: String? = null,
    var bankProvinceId: Int? = null,
    var checkKey: String? = null,
    var cityId: Int? = null,
    var contactName: String? = null,
    var contactPhone: String? = null,
    var deviceImage: String? = null,
    var districtId: Int? = null,
    var doorImage: String? = null,
    var licenceForOpeningAccountImage: String? = null,
    var merchantNameAlias: String? = null,
    var merchantType: Int? = null,
    var provinceId: Int? = null,
    var subBankCode: String? = null,
    var subBankName: String? = null,

    var area: String? = null,
    var bankArea: String? = null,
    val id: Int? = null,
    val state: Int? = null,
) : BaseObservable() {

    @get:Bindable
    var bankAccountNoVal: String
        get() = bankAccountNo ?: ""
        set(value) {
            bankAccountNo = value
            notifyPropertyChanged(BR.bankAccountNoVal)
        }

    @get:Bindable
    var areaVal: String
        get() = area ?: ""
        set(value) {
            area = value
            notifyPropertyChanged(BR.areaVal)
        }

    @get:Bindable
    var bankCodeVal: String
        get() = bankCode ?: ""
        set(value) {
            bankCode = value
            notifyPropertyChanged(BR.bankCodeVal)
        }

    @get:Bindable
    var bankNameVal: String
        get() = bankName ?: ""
        set(value) {
            bankName = value
            notifyPropertyChanged(BR.bankNameVal)
        }

    @get:Bindable
    var subBankNameVal: String
        get() = subBankName ?: ""
        set(value) {
            subBankName = value
            notifyPropertyChanged(BR.subBankNameVal)
        }

    @get:Bindable
    var subBankCodeVal: String
        get() = subBankCode ?: ""
        set(value) {
            subBankCode = value
            notifyPropertyChanged(BR.subBankCodeVal)
        }

    @get:Bindable
    var bankMobileNoVal: String
        get() = bankMobileNo ?: ""
        set(value) {
            bankMobileNo = value
            notifyPropertyChanged(BR.bankMobileNoVal)
        }
}