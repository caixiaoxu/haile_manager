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
    var shopName: String? = null,
    var managerName: String? = null,
    var phone: String? = null,
    var code: String? = null,
    var password: String? = null,
    @Transient
    var passwordAgain: String? = null,
    var provinceId: Int? = null,
    var provinceName: String? = null,
    var cityId: Int? = null,
    var cityName: String? = null,
    var areaId: Int? = null,
    var areaName: String? = null,
    var address: String? = null,
) : BaseObservable() {

    @get:Bindable
    var shopNameVal: String
        get() = shopName ?: ""
        set(value) {
            shopName = value
            notifyPropertyChanged(BR.shopNameVal)
        }

}
