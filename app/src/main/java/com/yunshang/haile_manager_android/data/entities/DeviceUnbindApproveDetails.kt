package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/17 10:53
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceUnbindApproveDetails(
    val auditTime: String? = null,
    val categoryId: Int? = null,
    val categoryName: String? = null,
    val createTime: String? = null,
    val creatorAccount: String? = null,
    val creatorId: Int? = null,
    val goodsId: Int? = null,
    val goodsName: String? = null,
    val id: Int? = null,
    val imei: String? = null,
    val positionId: Int? = null,
    val positionName: String? = null,
    val remark: String? = null,
    val shopId: Int? = null,
    val shopName: String? = null,
    val status: Int? = null
){

    fun statusVal(): String = when (status) {
        1 -> "未处理"
        2 -> "批准"
        3 -> "驳回"
        else -> ""
    }
}