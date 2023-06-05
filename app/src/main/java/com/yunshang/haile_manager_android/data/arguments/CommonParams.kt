package com.yunshang.haile_manager_android.data.arguments

import androidx.annotation.IntDef
import androidx.annotation.StringDef

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/26 13:44
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object DeviceCategory {
    const val CategoryId = "categoryId"
    const val CategoryCode = "categoryCode"
    const val CommunicationType = "communicationType"

    // 洗衣机
    const val Washing = "00"

    // 洗鞋机
    const val Shoes = "01"

    // 烘干机
    const val Dryer = "02"

    // 吹风机
    const val Hair = "03"

    // 饮水机
    const val Water = "04"

    @StringDef(Washing, Shoes, Dryer, Hair, Water)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IDeviceCategoryType

    /**
     * 是否是烘干机
     */
    fun isDryer(categoryCode: String?) = Dryer == categoryCode

    /**
     * 是否是脉冲设备
     * 10-串口 20-脉冲
     */
    fun isPulseDevice(communicationType: Int?) = 20 == communicationType
}

object SearchType {
    const val SearchType = "searchType"

    // 搜索设备
    const val Device = 0

    // 搜索店铺
    const val Shop = 1

    // 搜索订单
    const val Order = 2

    @IntDef(Device, Shop, Order)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ISearchType
}