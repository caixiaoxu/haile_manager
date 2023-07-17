package com.yunshang.haile_manager_android.data.common

import androidx.annotation.IntDef
import androidx.annotation.StringDef
import com.yunshang.haile_manager_android.R

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

    // 投放器
    const val Dispenser = "09"

    @StringDef(Washing, Shoes, Dryer, Hair, Water, Dispenser)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IDeviceCategoryType

    /**
     * 是否是烘干机
     */
    fun isDryer(categoryCode: String?) = Dryer == categoryCode

    /**
     * 是否是吹风机
     */
    fun isHair(categoryCode: String?) = Hair == categoryCode

    /**
     * 是否是烘干机
     */
    fun isDryerOrHair(categoryCode: String?) = Dryer == categoryCode || Hair == categoryCode

    /**
     * 是否是脉冲设备
     * 10-串口 20-脉冲
     */
    fun isPulseDevice(communicationType: Int?) = 20 == communicationType

    /**
     * 可显示的设备类型
     */
    fun canShowDeviceCategory(categoryCode: String): Boolean =
        categoryCode in arrayOf(Washing, Shoes, Dryer, Hair)
}

object SearchType {
    const val SearchType = "searchType"

    // 搜索设备
    const val Device = 0

    // 搜索店铺
    const val Shop = 1

    // 搜索订单
    const val Order = 2

    // 搜索预约订单
    const val AppointOrder = 3

    @IntDef(Device, Shop, Order, AppointOrder)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ISearchType
}

object RechargeType {
    fun getMainRes(type: Int, subType: Int): Int {
        var mainRes: Int
        if (100 == type) {
            mainRes = R.mipmap.icon_haixin_recharge_list_main
        } else {
            mainRes = R.mipmap.icon_haixin_expense
        }

        if (104 == subType || 203 == subType) {
            mainRes = R.mipmap.icon_haixin_refund
        } else if (202 == subType) {
            mainRes = R.mipmap.icon_haixin_recycle
        }
        return mainRes
    }
}