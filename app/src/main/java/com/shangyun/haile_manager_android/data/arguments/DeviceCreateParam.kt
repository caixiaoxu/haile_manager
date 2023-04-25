package com.shangyun.haile_manager_android.data.arguments

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/24 17:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceCreateParam(
    var id: Int = -1,//商品id
    var shopId: Int = -1,//	店铺id
    var spuId: Int = -1,//设备型号id
    var shopCategoryId: Int = -1,//	店铺分类id
    var name: String = "",//	设备名称
    var imei: String = "'",//	IMEI
    var code: String = "",//付款码
    var extAttr: String = "",//	附加属性
    var soldState: Int = -1,//售卖状态。1上架，2下架
    var items: List<DeviceCreateItem> = arrayListOf(),//	功能列表
    var communicationType: Int = -1,
)

data class DeviceCreateItem(
    val extAttr: String,
    val feature: String,
    val id: Int,
    val name: String,
    val price: String,
    val pulse: Int,
    val skuId: Int,
    val soldState: Int,
    val unit: String
)