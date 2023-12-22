package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/21 11:18
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class IssueInvoiceDetailsEntity(
    val invoiceTemplateId: Int? = null,
    val chargeType: Int? = null,
    val amount: Double? = null,
    val code: String? = null,
    val createTime: String? = null,
    val creatorId: Int? = null,
    val id: Int? = null,
    val invoiceTemplate: InvoiceTemplate? = null,
    val lastEditor: Int? = null,
    val logisticsName: String? = null,
    val logisticsNo: String? = null,
    val status: Int? = null,
    val type: Int? = null,
    val updateTime: String? = null,
    val userId: Int? = null,
    val address: String? = null,
    val auditingDate: String? = null,
    val cityId: Int? = null,
    val cityName: String? = null,
    val creatorName: String? = null,
    val districtId: Int? = null,
    val districtName: String? = null,
    val email: String? = null,
    val provinceId: Int? = null,
    val provinceName: String? = null,
    val `receiver`: String? = null,
    val smsPhone: String? = null,
) {
    fun areaVal(): String =
        if (provinceName.isNullOrEmpty() || cityName.isNullOrEmpty() || districtName.isNullOrEmpty()) "" else "$provinceName-$cityName-$districtName"
}

data class InvoiceTemplate(
    val address: String? = null,
    val bankAccount: String? = null,
    val bankName: String? = null,
    val business: String? = null,
    val context: String? = null,
    val email: String? = null,
    val id: Int? = null,
    val isDefault: Int? = null,
    val isPersonal: Int? = null,
    val name: String? = null,
    val phone: String? = null,
    val `receiver`: String? = null,
    val smsPhone: String? = null,
    val taxNo: String? = null,
    val taxpayer: String? = null,
    val title: String? = null,
    val type: Int? = null,
    val userId: Int? = null,
)