package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.rule.ISearchSelectEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/18 10:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SchoolSelectEntity(
    val id: Int,
    val name: String,//名称
    val type: Int,//组织类型<1：学校>
    val lng: Double,//经度
    val lat: Double,//纬度
    val provinceName: String,//省
    val cityName: String,//市
    val districtName: String,//区
    val provinceId: Int,//省id
    val cityId: Int,//市id
    val districtId: Int,//区id
    val address: String,//详细地址
    var subType: String = "",//学校类弄
    var attribute: Int = -1,//学校属性
    var createTime: String = ""//创建时间
) : ISearchSelectEntity {
    override fun getTitle(): String = name

    override fun getContent(): Array<String> =
        arrayOf(provinceName + cityName + districtName + address)
}
