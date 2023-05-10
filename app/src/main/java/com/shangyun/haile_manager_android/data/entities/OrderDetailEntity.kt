package com.shangyun.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/9 17:59
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class OrderDetailEntity(
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
    val discountPrice: Double,
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
    val payNo: String,
    val payPrice: Double,
    val payTime: String,
    val promotionList: List<Promotion>,
    val realPrice: Double,
    val refundBy: String,
    val refundDesc: String,
    val refundNo: String,
    val refundPrice: Double,
    val refundTime: String,
    val shopId: Int,
    val shopName: String,
    val skuList: List<Sku>,
    val state: Int
){

}