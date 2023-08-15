package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity
import com.yunshang.haile_manager_android.data.arguments.BusinessHourParams
import com.yunshang.haile_manager_android.data.arguments.ShopParam

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
data class ShopDetailEntity(
    val id: Int,
    val name: String,// 店铺名称
    val shopType: Int, // 店铺类型
    val shopTypeName: String, //店铺类型类型名称
    val state: Int, // 状态 1营业；2：暂停
    val createName: String,// 创建人
    val createTime: String,// 创建时间
    val schoolId: Int,// 学校id
    val schoolName: String,// 学校名
    val provinceId: Int, //省id
    val cityId: Int,// 市id
    val districtId: Int,// 区id
    val provinceName: String,// 省
    val cityName: String,// 市
    val districtName: String,// 区
    val area: String,// 小区/大厦/学校
    val address: String,// 详细地址/楼层
    val lat: Double,// 经度
    val lng: Double,// 纬度
    val workTime: String,// 营业时间
    val workTimeStr: String,// 营业时间
    val serviceTelephone: String,// 客服电话
    val shopBusiness: String, // 店铺业务
    val businessName: List<ShopBusinessTypeEntity>,// 店铺业务名称
    val attribute: Int,// 店铺属性1联营店,0非联营
    val appointSettingList: List<AppointSetting>?,//预约开关
    val paymentSettings: ShopPaySettingsEntity? //
) {
    fun getRealAddress(): String = if (1 == shopType) area else address
    fun hasSchoolName(): Boolean = 0 != schoolId && !schoolName.isNullOrEmpty()
    fun getSchoolNameTitle(): String = StringUtils.getString(R.string.school_name)
    fun hasArea(): Boolean = 0 != provinceId && 0 != cityId && 0 != districtId
    fun getAreaTitle(): String = StringUtils.getString(R.string.area)
    fun getBusinessNameVal(): String = businessName.joinToString("、") { it.businessName }

    fun hasBusinessName(): Boolean = !businessName.isNullOrEmpty()
    fun getBusinessNameTitle(): String = StringUtils.getString(R.string.business_type)
    fun hasLocation(): Boolean = !getRealAddress().isNullOrEmpty()
    fun getLocationTitle(): String = StringUtils.getString(R.string.location_detail)
    fun hasWorkTime(): Boolean = !workTime.isNullOrEmpty() || !workTimeStr.isNullOrEmpty()

    fun workTimeArr(): MutableList<BusinessHourEntity>? =
        GsonUtils.json2List(workTimeStr, BusinessHourParams::class.java)?.let { params ->
            params.map {
                BusinessHourEntity().apply {
                    formatData(it.weekDays, it.workTime)
                }
            }.toMutableList()
        } ?: run {
            mutableListOf(
                BusinessHourEntity(ShopParam.businessDay, workTime)
            )
        }

    fun workTimeVal(): String = workTimeArr()?.let {
        it.joinToString("\n") { item ->
            item.hourWeekVal + item.workTime
        }
    } ?: workTime

    fun getWorkTimeTitle(): String = StringUtils.getString(R.string.business_hours)
    fun hasCreator(): Boolean = !createName.isNullOrEmpty()
    fun getCreatorTitle(): String = StringUtils.getString(R.string.creator)
    fun hasCreateTime(): Boolean = !createTime.isNullOrEmpty()
    fun getCreateTimeTitle(): String = StringUtils.getString(R.string.create_time)

}

data class AppointSetting(
    val goodsCategoryId: Int,
    val goodsCategoryName: String, // 开关名称
    val appointSwitch: Int // 开关 0--关， 1--开
)