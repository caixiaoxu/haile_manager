package com.yunshang.haile_manager_android.data.common

import androidx.annotation.IntDef
import androidx.annotation.StringDef
import com.lsy.framelib.utils.StringUtils
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
    const val IgnorePayCodeFlag = "ignorePayCodeFlag"

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

    // 沐浴
    const val Shower = "08"

    // 投放器
    const val Dispenser = "09"

    @StringDef(Washing, Shoes, Dryer, Hair, Water, Dispenser, Shower)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IDeviceCategoryType

    /**
     * 是否是洗衣机
     */
    fun isWashing(categoryCode: String?) = Washing == categoryCode

    /**
     * 是否是洗鞋机
     */
    fun isShoes(categoryCode: String?) = Shoes == categoryCode

    /**
     * 是否是洗衣机或洗鞋机
     */
    fun isWashingOrShoes(categoryCode: String?) = Washing == categoryCode || Shoes == categoryCode

    /**
     * 是否是烘干机
     */
    fun isDryer(categoryCode: String?) = Dryer == categoryCode

    /**
     * 是否是吹风机
     */
    fun isHair(categoryCode: String?) = Hair == categoryCode

    /**
     * 是否是投放器
     */
    fun isDispenser(categoryCode: String?) = Dispenser == categoryCode

    /**
     * 是否是饮水机或淋浴
     */
    fun isDrinkingOrShower(categoryCode: String?) = Water == categoryCode || Shower == categoryCode

    /**
     * 是否是饮水机
     */
    fun isDrinking(categoryCode: String?) = Water == categoryCode

    /**
     * 是否是淋浴
     */
    fun isShower(categoryCode: String?) = Shower == categoryCode


    /**
     * 是否是烘干机
     */
    fun isDryerOrHair(categoryCode: String?) = Dryer == categoryCode || Hair == categoryCode

    /**
     * 是否是脉冲设备
     * 10-串口 20-脉冲
     */
    fun isPulseDevice(communicationType: Int?) = 20 == communicationType

    fun deviceCategoryName(categoryCode: String?): String = when (categoryCode) {
        Washing -> StringUtils.getString(R.string.wash)
        Shoes -> StringUtils.getString(R.string.shoes)
        Dryer -> StringUtils.getString(R.string.dryer)
        Hair -> StringUtils.getString(R.string.hair)
        Water -> StringUtils.getString(R.string.water)
        Dispenser -> StringUtils.getString(R.string.dispenser)
        Shower -> StringUtils.getString(R.string.shower)
        else -> ""
    }
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

    // 充值退款
    const val HaiXinRefundRecord = 4

    // 充值用户
    const val HaiXinRechargeAccount = 5

    // 分账
    const val SubAccount = 6

    // 设备报修
    const val DeviceRepairs = 7

    // 优惠券搜索
    const val Coupon = 8

    @IntDef(
        Device,
        Shop,
        Order,
        AppointOrder,
        HaiXinRefundRecord,
        HaiXinRechargeAccount,
        SubAccount,
        DeviceRepairs,
        Coupon
    )
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