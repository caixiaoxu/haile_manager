package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.data.entities.ShopPaySettingsEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/14 15:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopCreateParam(
    var id: Int? = null,
    var shopType: Int = -1,
    var name: String = "",
    var provinceId: Int = -1,
    var cityId: Int = -1,
    var districtId: Int = -1,
    var area: String = "",
    var address: String = "",
    var lng: Double? = null,
    var lat: Double? = null,
    var schoolName: String = "",
    var workTime: String = "",
    var serviceTelephone: String = "",
    var shopBusiness: String = "",
    var schoolId: Int = -1,
    @SerializedName("paymentSettings")
    var _paymentSettings: ShopPaySettingsEntity? = null
) : BaseObservable() {

    @get:Bindable
    var paymentSettings: ShopPaySettingsEntity?
        get() = _paymentSettings
        set(value) {
            _paymentSettings = value
            notifyPropertyChanged(BR.paymentSettings)
        }
}