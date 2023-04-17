package com.shangyun.haile_manager_android.data.entities

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
data class ShopDetailsEntity(
    val id: Int? = null,
    var shopType: Int = -1,
    var name: String = "",
    var provinceId: String = "",
    var cityId: String = "",
    var districtId: String = "",
    var area: String = "",
    var address: String = "",
    var lng: Int = -1,
    var lat: Int = -1,
    var schoolName: String = "",
    var workTime: String = "",
    var serviceTelephone: String = "",
    var shopBusiness: String = "",
    var schoolId: Int = -1
)