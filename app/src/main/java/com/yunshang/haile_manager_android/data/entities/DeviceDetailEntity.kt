package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.common.DeviceCategory.Dispenser
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
    val items: MutableList<SkuFunConfigurationV2Param>,
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
    val dosingVOS: List<DosingVOS>?,
    val errorDeviceOrderId: Int,
    val errorDeviceOrderNo: String,
    val spuDto: Spu?,
) {

    val isDispenser: Boolean
        get() = DeviceCategory.isDispenser(categoryCode)

    fun getDeviceDetailMain(): Int = when (categoryCode) {
        DeviceCategory.Washing -> R.mipmap.icon_device_detail_washing_main
        DeviceCategory.Dryer -> R.mipmap.icon_device_detail_dryer_main
        DeviceCategory.Shoes -> R.mipmap.icon_device_detail_shose_main
        DeviceCategory.Hair -> R.mipmap.icon_device_detail_hair_main
        DeviceCategory.Dispenser -> R.mipmap.icon_device_detail_dispenser_main
        DeviceCategory.Water -> R.mipmap.icon_device_detail_drinking_main
        else -> R.mipmap.icon_device_detail_washing_main
    }

    fun getReason() = "${deviceErrorMsg}${deviceErrorCode}"

    fun hasName(): Boolean = !name.isNullOrEmpty()

    fun getNameTitle(): String = StringUtils.getString(R.string.device_name)
    fun getAssociationNameTitle(): String = StringUtils.getString(R.string.device_name)
    fun getDeviceStateTitle(): String = StringUtils.getString(R.string.device_status)
    fun getDeviceOrderTitle(): String = StringUtils.getString(R.string.device_order)
    fun getDeviceSinglePulseTitle(): String = StringUtils.getString(R.string.single_pulse_quantity)
    fun getAssociationTypeTitle(): String = StringUtils.getString(R.string.device_category)
    fun getAssociationImeiTitle(): String = StringUtils.getString(R.string.imei)
    fun showOrderNo(): Boolean = errorDeviceOrderNo.isNullOrEmpty()

    fun getLaundryStateName(): String {
        if (null == dosingVOS) return ""
        dosingVOS.find { it.liquidType == 1 }.let {
            return when (it?.liquidStatus) {
                0 -> "正常"
                1 -> "液量低"
                2 -> "缺液"
                else -> ""
            }
        }
        return ""
    }

    fun getRemainingStateName(): String {
        if (null == dosingVOS) return ""
        dosingVOS.find { it.liquidType == 2 }.let {
            return when (it?.liquidStatus) {
                0 -> "正常"
                1 -> "液量低"
                2 -> "缺液"
                else -> ""
            }
        }
        return ""
    }

    fun showLaundryState(): Boolean {
        if (null == dosingVOS) return false
        dosingVOS.find { it.liquidType == 1 }.let {
            return (it?.liquidStatus == 1 || it?.liquidStatus == 2)
        }
        return false
    }

    fun showRemainingState(): Boolean {
        //0正常，1液量低，2缺液
        if (null == dosingVOS) return false
        dosingVOS.find { it.liquidType == 2 }.let {
            return (it?.liquidStatus == 1 || it?.liquidStatus == 2)
        }
        return false
    }

    fun LaundryNumber(): String {
        if (null == dosingVOS) return "0%"
        dosingVOS.find { it.liquidType == 1 }.let {
            return "${it?.liquidRemaining ?: 0}%"
        }
        return "0%"
    }

    fun RemainingNumber(): String {
        if (null == dosingVOS) return "0%"
        dosingVOS.find { it.liquidType == 2 }.let {
            return "${it?.liquidRemaining ?: 0}%"
        }
        return "0%"
    }

    fun showRelated(): Boolean {
        if (null == relatedGoodsDetailVo) return false
        if (relatedGoodsDetailVo.dosingVOS.isNullOrEmpty()) return false
        if (categoryCode == Dispenser) return false
        return true
    }

    fun getSinglePulseQuantity() =
        items.firstOrNull()?.extAttrDto?.items?.firstOrNull()?.pulseVolumeFactor?.let { it + "ml" }
            ?: ""

    fun hasQrId(): Boolean = 0 < id

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

    /**
     * 获取状态
     * 10-空闲；20-工作中；30-故障；40-停用
     */
    fun getDeviceStatusValue(): String = when (workStatus) {
        10 -> "空闲"
        20 -> "运行中"
        30 -> "故障"
        40 -> "停用"
        else -> ""
    }

    fun toUpdateParams() = hashMapOf(
        "id" to id,
        "name" to name,
        "imei" to imei,
        "code" to code,
        "extAttr" to extAttr,
        "soldState" to soldState,
        "items" to items,
    )
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
            soldState,
            "",
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
    val name: String,
    val categoryName: String,
    val categoryCode: String,
    val dosingVOS: List<DosingVOS>,
)

data class DosingVOS(
    val liquidRemaining: Int,
    val liquidStatus: Int,
    val liquidType: Int,
    val configs: List<DosingConfigs>?,
)

data class DosingConfigs(
    val amount: Int,
    val itemId: Int,
    val liquidType: Int,
    val liquidTypeId: Int,
    val price: Double,
    val name: String,
    var isDefault: Boolean,
    val isOn: Boolean,
)