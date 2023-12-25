package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.rule.ICommonNewBottomItemEntity

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
): ICommonNewBottomItemEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InvoiceReceiverEntity) return false
        else if (id == other.id) return true
        return super.equals(other)
    }
}