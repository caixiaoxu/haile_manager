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
    var address: String,
    var authCode: String,
    var bankAccountName: String,
    var bankAccountNo: String,
    var bankCardImage: String,
    var bankCityId: Int,
    var bankCode: String,
    var bankDistrictId: Int,
    var bankMobileNo: String,
    var bankName: String,
    var bankProvinceId: Int,
    var checkKey: String,
    var cityId: Int,
    var contactName: String,
    var contactPhone: String,
    var deviceImage: String,
    var districtId: Int,
    var doorImage: String,
    var licenceForOpeningAccountImage: String,
    var merchantNameAlias: String,
    var merchantType: Int,
    var provinceId: Int,
    var subBankCode: String,
    var subBankName: String,

    var area: String? = null,
    var bankArea: String? = null,
    val id: Int? = null,
    val state: Int? = null,
) : BaseObservable() {

    @get:Bindable
    var bankAccountNoVal: String
        get() = bankAccountNo
        set(value) {
            bankAccountNo = value
            notifyPropertyChanged(BR.bankAccountNoVal)
        }

    @get:Bindable
    var areaVal: String
        get() = area ?:""
        set(value) {
            area = value
            notifyPropertyChanged(BR.areaVal)
        }

    @get:Bindable
    var bankNameVal: String
        get() = bankName
        set(value) {
            bankName = value
            notifyPropertyChanged(BR.bankNameVal)
        }

    @get:Bindable
    var subBankNameVal: String
        get() = subBankName
        set(value) {
            subBankName = value
            notifyPropertyChanged(BR.subBankNameVal)
        }

    @get:Bindable
    var subBankCodeVal: String
        get() = subBankCode
        set(value) {
            subBankCode = value
            notifyPropertyChanged(BR.subBankCodeVal)
        }

    @get:Bindable
    var bankMobileNoVal: String
        get() = bankMobileNo
        set(value) {
            bankMobileNo = value
            notifyPropertyChanged(BR.bankMobileNoVal)
        }
}