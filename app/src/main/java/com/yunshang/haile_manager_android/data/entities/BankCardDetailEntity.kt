package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable

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

    val area: String? = null,
    val bankArea: String? = null,
    val id: Int? = null,
    val state: Int? = null,
): BaseObservable() {

}