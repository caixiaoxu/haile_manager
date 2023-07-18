package com.yunshang.haile_manager_android.data.arguments

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
    var schoolId: Int = -1
)