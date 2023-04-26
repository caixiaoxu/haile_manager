package com.shangyun.haile_manager_android.data.arguments

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

    //洗衣机
    const val Washing = "00"

    //洗鞋机
    const val Shoes = "01"

    //烘干机
    const val Dryer = "02"

    @StringDef(Washing, Shoes, Dryer)
    @Retention(AnnotationRetention.SOURCE)
    annotation class IDeviceCategoryType

    /**
     * 是否是烘干机
     */
    fun isDryer(categoryCode: String?) = DeviceCategory.Dryer == categoryCode

    /**
     * 是否是脉冲设备
     * 10-串口 20-脉冲
     */
    fun isPulseDevice(communicationType:Int?) = 20 == communicationType

}