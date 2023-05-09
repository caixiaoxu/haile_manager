package com.shangyun.haile_manager_android.data.entities

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
    val orderType: String,
    val originPrice: Int,
    val payMethod: String,
    val payMethodType: Int,
    val payTime: String,
    val promotionList: List<Promotion>,
    val realPrice: Int,
    val shopId: Int,
    val shopName: String,
    val skuList: List<Sku>,
    val state: Int
)

data class Promotion(
    val discountPrice: Int,
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
    val skuUnit: Int,
    val state: Int
)