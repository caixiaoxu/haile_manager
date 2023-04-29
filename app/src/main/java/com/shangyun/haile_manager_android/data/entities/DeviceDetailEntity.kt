package com.shangyun.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R

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
    val name: String,
    val feature: String,
    val mainPic: String,
    val mainVideo: String,
    val code: String,
    val tags: String,
    val soldState: Int,
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
    val imei: String,
    val communicationType: Int,
    val company: String,
    val workStatus: Int,
    val deviceErrorCode: String,
    val deviceErrorMsg: String,
    val scanUrl: String,
) {
    fun getReason() = "${deviceErrorMsg}${deviceErrorCode}"

    fun hasName(): Boolean = !name.isNullOrEmpty()

    fun getNameTitle(): String = StringUtils.getString(R.string.device_name)

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
) {

    fun getTitle() =
        if (pulse.isNullOrEmpty())
            StringUtils.getString(R.string.func_price_item1, name, price, unit)
        else StringUtils.getString(R.string.func_price_item2, name, price, unit, pulse)

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

        return SkuFuncConfigurationParam(skuId, name, priceV,pulseV,unitV,extAttr,feature,soldState)
    }
}