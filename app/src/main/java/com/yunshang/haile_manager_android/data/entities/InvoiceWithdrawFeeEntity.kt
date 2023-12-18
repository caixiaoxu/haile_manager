package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/18 15:49
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class InvoiceWithdrawFeeEntity(
    val account: String? = null,
    val accountId: Int? = null,
    val accountName: String? = null,
    val applyTime: String? = null,
    val cashOutAccount: String? = null,
    val cashOutNo: String? = null,
    val fee: String? = null,
    val invoiceCreateTime: String? = null,
    val invoiceCreatorId: Int? = null,
    val invoiceCreatorName: String? = null,
    val invoiceStatus: Int? = null
){
    @Transient
    @get:Bindable
    var selected:Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}