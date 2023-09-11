package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

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
    val stateTip: String? = null,
) : BaseObservable() {

    @get:Bindable
    var bankAccountNoVal: String
        get() = bankAccountNo ?: ""
        set(value) {
            bankAccountNo = value
            notifyPropertyChanged(BR.bankAccountNoVal)
        }

    @get:Bindable
    var bankAreaVal: String
        get() = bankArea ?: ""
        set(value) {
            bankArea = value
            notifyPropertyChanged(BR.bankAreaVal)
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

    @get:Bindable
    var merchantNameAliasVal: String
        get() = merchantNameAlias ?: ""
        set(value) {
            merchantNameAlias = value
            notifyPropertyChanged(BR.merchantNameAliasVal)
        }

    @get:Bindable
    var areaVal: String
        get() = area ?: ""
        set(value) {
            area = value
            notifyPropertyChanged(BR.areaVal)
        }

    @get:Bindable
    var addressVal: String
        get() = address ?: ""
        set(value) {
            address = value
            notifyPropertyChanged(BR.addressVal)
        }

    @get:Bindable
    var contactNameVal: String
        get() = contactName ?: ""
        set(value) {
            contactName = value
            notifyPropertyChanged(BR.contactNameVal)
        }

    @get:Bindable
    var contactPhoneVal: String
        get() = contactPhone ?: ""
        set(value) {
            contactPhone = value
            notifyPropertyChanged(BR.contactPhoneVal)
        }

    val stateVal: String
        get() = state?.let { StringUtils.getStringArray(R.array.bank_card_state_arr)[state] } ?: ""

    val typeTitle: String
        get() = StringUtils.getString(R.string.type)

    val typeVal: String
        get() = if (null != merchantType && merchantType!! > 0) StringUtils.getStringArray(R.array.verify_type_arr)[merchantType!! - 1] else ""

    val bankAccountTitle: String
        get() = StringUtils.getString(R.string.bank_account)

    val accountNameTitle: String
        get() = StringUtils.getString(R.string.account_name)

    val accountNameVal: String
        get() = bankAccountName ?: ""

    val openBankAreaTitle: String
        get() = StringUtils.getString(R.string.open_bank_area)

    val openBankSubBranchTitle: String
        get() = StringUtils.getString(R.string.open_bank_sub_branch)

    val bankSubBranchLinesNoTitle: String
        get() = StringUtils.getString(R.string.bank_sub_branch_lines_no)

    val openBankReservedPhoneTitle: String
        get() = StringUtils.getString(R.string.open_bank_reserved_phone)

    val bankCardPic: String
        get() = (if (2 == merchantType) licenceForOpeningAccountImage else bankCardImage) ?: ""

    val shopSimpleNameTitle: String
        get() = StringUtils.getString(R.string.shop_simple_name)

    val areaTitle: String
        get() = StringUtils.getString(R.string.shop_area)

    val addressTitle: String
        get() = StringUtils.getString(R.string.shop_location_detail)

    val managerTitle: String
        get() = StringUtils.getString(R.string.shop_manager)

    val managerPhoneTitle: String
        get() = StringUtils.getString(R.string.shop_manager_phone)
}