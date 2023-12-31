package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/11 10:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class RealNameAuthDetailEntity(
    @SerializedName("companyLicense")
    var _companyLicense: String? = null,
    var companyName: String? = null,
    var companyUsci: String? = null,
    val id: Int? = null,
    @SerializedName("idCardExpirationDate")
    var _idCardExpirationDate: String? = null,
    @SerializedName("idCardExpirationType")
    var _idCardExpirationType: Int? = null,
    @SerializedName("idCardFont")
    var _idCardFont: String? = null,
    var idCardName: String? = null,
    var idCardNo: String? = null,
    @SerializedName("idCardReverse")
    var _idCardReverse: String? = null,
    var reason: String? = null,
    var status: Int? = null,
    var verifyType: Int? = null,
    var authCode: String? = null,
) : BaseObservable() {

    val verifyStatusValue: String
        get() = status?.let {
            StringUtils.getStringArray(R.array.verify_status_arr)[status!! - 1]
        } ?: ""

    @get:Bindable
    val verifyTypeName: String
        get() = if (null != verifyTypeVal && verifyTypeVal!! > 0)
            StringUtils.getStringArray(R.array.verify_type_arr)[verifyTypeVal!! - 1]
        else ""

    @get:Bindable
    var verifyTypeVal: Int?
        get() = verifyType
        set(value) {
            verifyType = value
            notifyPropertyChanged(BR.verifyTypeVal)
            notifyPropertyChanged(BR.verifyTypeName)
        }

    @get:Bindable
    val idCardExpirationTypeName: String
        get() = if (null != idCardExpirationType && idCardExpirationType!! > 0)
            StringUtils.getStringArray(R.array.indate_type_arr)[idCardExpirationType!! - 1]
        else ""

    @get:Bindable
    var idCardExpirationType: Int?
        get() = _idCardExpirationType
        set(value) {
            _idCardExpirationType = value
            notifyPropertyChanged(BR.idCardExpirationType)
            notifyPropertyChanged(BR.idCardExpirationTypeName)
        }

    @get:Bindable
    var idCardFont: String?
        get() = _idCardFont
        set(value) {
            _idCardFont = value
            notifyPropertyChanged(BR.idCardFont)
        }

    @get:Bindable
    var idCardReverse: String?
        get() = _idCardReverse
        set(value) {
            _idCardReverse = value
            notifyPropertyChanged(BR.idCardReverse)
        }

    @get:Bindable
    var companyLicense: String?
        get() = _companyLicense
        set(value) {
            _companyLicense = value
            notifyPropertyChanged(BR.companyLicense)
        }

    @get:Bindable
    var idCardExpirationDateValue: String?
        get() = idCardExpirationDate?.let { date ->
            date.split(",").let { arr ->
                if (arr.size >= 2) {
                    "${arr[0].trim()} 至 ${arr[1].trim()}"
                } else {
                    date.split(" - ").let { arr1 ->
                        if (arr1.size >= 2) {
                            "${arr1[0].trim()} 至 ${arr1[1].trim()}"
                        } else ""
                    }
                }
            }
        }
        set(value) {
            notifyPropertyChanged(BR.idCardExpirationDateValue)
        }

    var idCardExpirationDate: String?
        get() = _idCardExpirationDate
        set(value) {
            _idCardExpirationDate = value
            idCardExpirationDateValue = ""
        }

    val typeTitle: String
        get() = StringUtils.getString(R.string.type)

    val nameTitle: String
        get() = StringUtils.getString(if (3 == verifyTypeVal) R.string.legal_person_name else R.string.name)

    val idCardTitle: String
        get() = StringUtils.getString(if (3 == verifyTypeVal) R.string.legal_person_idcard else R.string.id_card)

    val indateTypeTitle: String
        get() = StringUtils.getString(R.string.indate_type)

    val idCardIndateTitle: String
        get() = StringUtils.getString(R.string.id_card_indate)

    val companyNameTitle: String
        get() = StringUtils.getString(R.string.company_name)

    val companyUsciTitle: String
        get() = StringUtils.getString(R.string.company_id)
}