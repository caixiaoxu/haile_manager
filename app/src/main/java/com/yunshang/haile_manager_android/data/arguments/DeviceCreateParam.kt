package com.yunshang.haile_manager_android.data.arguments

import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam

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
    var codeStr: String? = null,// 付款码原链接
    var extAttr: String = "",//	附加属性
    var washerImei: String? = null,//	洗衣机imei
    var soldState: Int = -1,//售卖状态。1上架，2下架
    var items: List<SkuFuncConfigurationParam> = arrayListOf(),//	功能列表
    @Transient
    var communicationType: Int = -1,
) {

    /**
     * 生成提交参数
     */
    fun toDeviceJson(): String =
        GsonUtils.any2Json(
            hashMapOf(
                "name" to name,
                "shopId" to shopId,
                "spuId" to spuId,
                "shopCategoryId" to shopCategoryId,
                "imei" to imei,
                "code" to if (imei == code) null else code,
                "codeStr" to codeStr,
                "extAttr" to extAttr,
                "items" to items,
                "washerImei" to washerImei,
            )
        )
}