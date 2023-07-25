package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/23 10:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceDetailEntity(
    val id: Int,
    val spuId: Int,
    val spuName: String,
    val spuFeature: String,
    val categoryId: Int,
    val categoryCode: String,
    val categoryName: String,
    val type: Int,
    var name: String,
    val feature: String,
    val mainPic: String,
    val mainVideo: String,
    var code: String,
    val tags: String,
    var soldState: Int,
    val soldStateOp: Int,
    val inventoryType: Int,
    val chargeUnit: Int,
    val price: Double,
    val extAttr: String,
    val creatorId: Int,
    val creatorName: String,
    val payType: String,
    val createTime: String,
    val updateTime: String,
    val brandId: Int,
    val shopCategoryId: Int,
    val organizationId: Int,
    val shopId: Int,
    val shopName: String,
    val items: List<Item>,
    var imei: String,
    val communicationType: Int,
    val company: String,
    val workStatus: Int,
    val deviceErrorCode: String,
    val deviceErrorMsg: String,
    var appointmentEnabled: Boolean?,
    val shopAppointmentEnabled: Boolean,
    val scanUrl: String,
    val qrId: Long, // 设备编号
    val deviceAttributeVo: DeviceAttributeVo,
    val relatedGoodsDetailVo: RelatedGoodsDetailVo,
    val dosingVOS: List<DosingVOS>,
    val errorDeviceOrderId: Int,
    val errorDeviceOrderNo: String,

    ) {
    fun getReason() = "${deviceErrorMsg}${deviceErrorCode}"

    fun hasName(): Boolean = !name.isNullOrEmpty()

    fun getNameTitle(): String = StringUtils.getString(R.string.device_name)
    fun getAssociationNameTitle(): String = StringUtils.getString(R.string.device_name)
    fun getDeviceStateTitle(): String = StringUtils.getString(R.string.device_status)
    fun getDeviceOrderTitle(): String = StringUtils.getString(R.string.device_order)
    fun getAssociationTypeTitle(): String = StringUtils.getString(R.string.device_category)
    fun getAssociationImeiTitle(): String = StringUtils.getString(R.string.imei)

    fun hasQrId(): Boolean = 0L < qrId

    fun getQrIdTitle(): String = StringUtils.getString(R.string.device_no)

    fun hasShop(): Boolean = 0 != shopId && !shopName.isNullOrEmpty()

    fun getShopTitle(): String = StringUtils.getString(R.string.department)

    fun hasCategory(): Boolean =
        0 != categoryId && !categoryCode.isNullOrEmpty() && !categoryName.isNullOrEmpty()

    fun getCategoryTitle(): String = StringUtils.getString(R.string.device_category)

    fun hasModel(): Boolean = 0 != spuId && !spuName.isNullOrEmpty()

    fun getModelTitle(): String = StringUtils.getString(R.string.device_model)

    fun getCommunicationTypeStr(): String =
        StringUtils.getString(if (20 == communicationType) R.string.pulse else R.string.serial_port)

    fun hasCommunicationType(): Boolean = 0 != communicationType

    fun getCommunicationTypeTitle(): String = StringUtils.getString(R.string.communication_type)

    fun hasCompany(): Boolean = !company.isNullOrEmpty()

    fun getCompanyTitle(): String = StringUtils.getString(R.string.company)

    fun hasPayCode(): Boolean = !code.isNullOrEmpty()

    fun getPayCodeTitle(): String = StringUtils.getString(R.string.pay_code)
    fun hasImeiCode(): Boolean = !imei.isNullOrEmpty()

    fun getImeiTitle(): String = StringUtils.getString(R.string.imei_title)
    fun hasCreator(): Boolean = 0 != creatorId && !creatorName.isNullOrEmpty()

    fun getCreatorTitle(): String = StringUtils.getString(R.string.creator)
    fun hasCreateTime(): Boolean = !createTime.isNullOrEmpty()

    fun getCreateTimeTitle(): String = StringUtils.getString(R.string.create_time)
}

data class Item(
    val id: Int,
    val skuId: Int,
    val name: String,
    val price: String,
    val pulse: String?,
    val unit: String,
    val extAttr: String,
    val feature: String,
    val soldState: Int,
) : ICommonBottomItemEntity {

    /**
     * 根据型号区分配置内容
     */
    fun getConfigurationStr(communicationType: Int, isDryer: Boolean): String =
        if (isDryer) {
            GsonUtils.json2List(extAttr, ExtAttrBean::class.java)?.joinToString("\n") {
                getSpec(
                    if (0.0 == it.price) "" else it.price.toString(),
                    if (0 == it.minutes) "" else it.minutes.toString(),
                    if (0 == it.pulse || !DeviceCategory.isPulseDevice(communicationType)) "" else it.pulse.toString()
                )
            } ?: ""
        } else {
            getSpec(price, unit, pulse)
        }

    fun getSpec(price: String, unit: String, pulse: String?) =
        if (pulse.isNullOrEmpty())
            StringUtils.getString(R.string.unit_device_configuration_hint3, price, unit)
        else
            StringUtils.getString(R.string.unit_device_configuration_hint4, price, unit, pulse)

    /**
     * 切换数据格式
     */
    fun changeConfigurationParam(): SkuFuncConfigurationParam? {
        val priceV: Double
        try {
            priceV = if (price.isNullOrEmpty()) 0.0 else price.toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        }
        val pulseV: Int
        try {
            pulseV = if (pulse.isNullOrEmpty()) 0 else pulse.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        }
        val unitV: Int
        try {
            unitV = if (unit.isNullOrEmpty()) 0 else unit.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        }

        return SkuFuncConfigurationParam(
            skuId,
            name,
            priceV,
            pulseV,
            unitV,
            extAttr,
            feature,
            soldState
        )
    }

    override fun getTitle(): String = name

}

data class DeviceAttributeVo(
    val maxTemperature: Int,
    val minTemperature: Int,
    val nowTemperature: Int,
    val preventDisturbStartTime: String,
    val preventDisturbStopTime: String,
    val preventDisturbSwitch: Boolean,
    val temperatureSwitch: Boolean,
    val voiceBroadcastStatus: Boolean,
    val volume: Int,
)

data class RelatedGoodsDetailVo(
    val imei: String,
    val spuName: String,
    val categoryName: String,
    val categoryCode: String,
)

data class DosingVOS(
    val liquidRemaining: Int,
    val liquidStatus: Int,
    val liquidType: Int,
    val configs: List<DosingConfigs>,
)

data class DosingConfigs(
    val amount: Int,
    val itemId: Int,
    val liquidType: Int,
    val liquidTypeId: Int,
    val price: Int,
    val name: String,
    var isDefault: Boolean,
    val isOn: Boolean,
)