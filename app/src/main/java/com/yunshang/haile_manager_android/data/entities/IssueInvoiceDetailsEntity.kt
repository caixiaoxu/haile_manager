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
    val amount: Int? = null,
    val chargeType: Int? = null,
    val code: String? = null,
    val createTime: String? = null,
    val creatorId: Int? = null,
    val id: Int? = null,
    val invoiceTemplate: InvoiceTemplate? = null,
    val invoiceTemplateId: Int? = null,
    val lastEditor: Int? = null,
    val logisticsName: String? = null,
    val logisticsNo: String? = null,
    val status: Int? = null,
    val type: Int? = null,
    val updateTime: String? = null,
    val userId: Int? = null
)

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
    val userId: Int? = null
)