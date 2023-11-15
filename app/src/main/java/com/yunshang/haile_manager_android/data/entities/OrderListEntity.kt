package com.yunshang.haile_manager_android.data.entities

import com.google.gson.annotations.SerializedName
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.extend.toRemove0Str
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/8 15:55
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class OrderListEntity(
    val buyerId: Int,
    val buyerName: String,
    val buyerPhone: String,
    val canCompensate: Boolean,
    val canRefund: Boolean,
    val canReset: Boolean,
    val canStart: Boolean,
    val createTime: String,
    val deviceName: String,
    val deviceSubType: String,
    val deviceType: String,
    val goodsCategoryIds: List<Any>,
    val id: Int,
    val orderCategory: Int,
    val orderNo: String,
    val orderSubCategory: Int,
    val orderSubType: Int,
    val orderType: Int,
    val originPrice: Double,
    val payMethod: String,
    val payMethodType: Int,
    val payTime: String,
    val promotionList: List<Promotion>,
    val realPrice: String,
    val shopId: Int,
    val shopName: String,
    val skuList: List<Sku>,
    val state: Int,
    val stateDesc: String,
    val appointmentState: Int,
    val canCancelReserve: Boolean,
    val endState: Int,
    val endStateDesc: String?,
    val refundTag: String? = null,
    val code: String? = null,
    val positionId: Int? = null,
    val positionName: String? = null
) : ISearchSelectEntity {
    override fun getSearchId(): Int = id

    override fun getTitle(): String = buyerPhone

    fun endStateVal(): String = if (1050 == endState) "故障结束订单" else ""

    fun shopPositionName(): String =
        shopName + if (positionName.isNullOrEmpty()) "" else "-$positionName"

    override fun getContent(): Array<String> = arrayOf(
        shopName,
        "$deviceName ${if (skuList.isNullOrEmpty()) "" else skuList.first().skuName}",
        orderNo
    )

    val hasRefundMoney: Boolean
        get() = refundTag?.split(",")?.contains("1") ?: false

    val hasRefundCoupon: Boolean
        get() = refundTag?.split(",")?.contains("2") ?: false

    fun getConfigureTitle(): String =
        skuList.sortedBy { item -> DeviceCategory.isDispenser(item.goodsCategoryCode) }
            .joinToString("+") { sku -> sku.configureTitle }
}

data class Promotion(
    val discountPrice: Double,
    val itemId: Int,
    val title: String
)

data class Sku(
    val canRefund: Boolean,
    val canReset: Boolean,
    val canStart: Boolean,
    val deviceSubType: String,
    val deviceType: String,
    val goodsCategoryId: Int,
    val id: Int,
    val originUnitPrice: Double,
    val realUnitPrice: Double,
    val skuName: String,
    val skuPrice: Double,
    val skuUnit: String,
    val unitCode: Int,
    val priceCalculateMode: Int,
    val state: Int,
    val discountPrice: String,
    val goodsCategoryCode: String,
    val goodsId: Int,
    @SerializedName("goodsItemInfo")
    val _goodsItemInfo: String,
    val goodsName: String,
    val num: Int,
    val orderNo: String,
    val originPrice: String,
    val realPrice: String,
    val imei: String? = null
) {
    val goodsItemInfo: GoodsItemInfoEntity?
        get() = GsonUtils.json2Class(_goodsItemInfo, GoodsItemInfoEntity::class.java)

    val unitValue: String
        get() = skuUnit.toRemove0Str() + if (1 == priceCalculateMode) {
            // 流量
            if (1 == unitCode) "ml" else "L"
        } else {
            //时间
            if (1 == unitCode) "秒" else "分钟"
        }

    val configureTitle: String
        get() = skuName + unitValue
}

data class GoodsItemInfoEntity(
    val priceCalculateMode: Int,
)