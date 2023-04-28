package com.shangyun.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/28 14:25
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceAdvancedSettingEntity(
    val baseFunctionCode: String,
    val baseFunctionName: String,
    val createTime: String,
    val extraAttr: String,
    val extraAttrHaiLi: String,
    val functionCode: String,
    val functionHaierCode: String,
    val functionHaierCodeV2: String,
    val functionHailiCode: String,
    val functionName: String,
    val id: Int,
    val modelCode: String,
    val modelName: String,
    val params: String,
    val price: Int,
    val remark: String,
    val status: String,
    val type: String,
    val updateTime: String,
    val waterWashTime: String,
    val workTime: Int
)