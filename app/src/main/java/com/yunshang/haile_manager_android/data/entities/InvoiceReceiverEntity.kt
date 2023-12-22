package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/22 14:46
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class InvoiceReceiverEntity(
    val id: Int? = null,
    val address: String? = null,
    val cityId: Int? = null,
    val cityName: String? = null,
    val districtId: Int? = null,
    val districtName: String? = null,
    val email: String? = null,
    val provinceId: Int? = null,
    val provinceName: String? = null,
    val `receiver`: String? = null,
    val smsPhone: String? = null,
    val userId: Int? = null
)