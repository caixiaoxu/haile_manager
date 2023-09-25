package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/21 17:29
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RegisterParams(
    var organizationName: String? = null,
    var realName: String? = null,
    var phone: String? = null,
    var code: String? = null,
    var password: String? = null,
    @Transient
    var passwordAgain: String? = null,
    var provinceId: Int? = null,
    var cityId: Int? = null,
    var districtId: Int? = null,
    var address: String? = null,
) : BaseObservable() {

    @get:Bindable
    var shopNameVal: String
        get() = organizationName ?: ""
        set(value) {
            organizationName = value
            notifyPropertyChanged(BR.shopNameVal)
        }

    @get:Bindable
    var managerNameVal: String
        get() = realName ?: ""
        set(value) {
            realName = value
            notifyPropertyChanged(BR.managerNameVal)
        }

    @get:Bindable
    var phoneVal: String
        get() = phone ?: ""
        set(value) {
            phone = value
            notifyPropertyChanged(BR.phoneVal)
        }

    @get:Bindable
    var codeVal: String
        get() = code ?: ""
        set(value) {
            code = value
            notifyPropertyChanged(BR.codeVal)
        }

    @get:Bindable
    var passwordVal: String
        get() = password ?: ""
        set(value) {
            password = value
            notifyPropertyChanged(BR.passwordVal)
        }

    @get:Bindable
    var passwordAgainVal: String
        get() = passwordAgain ?: ""
        set(value) {
            passwordAgain = value
            notifyPropertyChanged(BR.passwordAgainVal)
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
        }
}
