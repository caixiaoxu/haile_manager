package com.yunshang.haile_manager_android.data.entities

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
data class ShopAndPositionSearchEntity(
    val id: Int,
    val name: String,
    val provinceId: Int,
    val provinceName: String,
    val cityId: Int,
    val cityName: String,
    val districtId: Int,
    val districtName: String,
    val area: String,
    val address: String,
    val positionList: MutableList<ShopPositionSearch>? = null
) {
    fun getDetailAddress() = provinceName + cityName + districtName + area + address
}

data class ShopPositionSearch(
    val id: Int? = null,
    val name: String? = null
)