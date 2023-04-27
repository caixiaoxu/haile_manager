package com.shangyun.haile_manager_android.data.entities

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
    val amount: Int,
    val brandId: Int,
    val categoryCode: String,
    val categoryId: Int,
    val categoryName: String,
    val chargeUnit: Int,
    val code: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: String,
    val deleteFlag: Int,
    val extAttr: String,
    val feature: String,
    val id: Int,
    val inventoryType: Int,
    val items: List<Item>,
    val lastEditor: Int,
    val mainPic: String,
    val mainVideo: String,
    val name: String,
    val payType: String,
    val price: Int,
    val shopCategoryId: Int,
    val shopId: Int,
    val soldState: Int,
    val soldStateOp: Int,
    val specs: List<SpecX>,
    val spuId: Int,
    val spuName: String,
    val tags: String,
    val type: Int,
    val updateTime: String,
    val version: Int
)

data class Item(
    val amount: Int,
    val extAttr: String,
    val feature: String,
    val goodsId: Int,
    val id: Int,
    val items: String,
    val name: String,
    val price: Int,
    val skuCode: String,
    val skuId: Int,
    val soldState: Int,
    val spec: SpecX,
    val spuId: Int,
    val unit: Int
)

data class SpecX(
    val code: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: Int,
    val description: String,
    val id: Int,
    val name: String,
    val sort: Int,
    val status: Int,
    val updateTime: String,
    val values: List<Value>
)

data class Value(
    val code: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: String,
    val description: String,
    val ext: Ext,
    val id: Int,
    val image: String,
    val lastEditor: Int,
    val name: String,
    val specId: Int,
    val specName: String,
    val updateTime: String
)