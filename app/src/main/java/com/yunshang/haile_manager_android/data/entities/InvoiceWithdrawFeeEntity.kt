package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

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
    val fee: Double = 0.00,
    val invoiceCreateTime: String? = null,
    val invoiceCreatorId: Int? = null,
    val invoiceCreatorName: String? = null,
    val invoiceStatus: Int? = null,
    val cashOutType: String? = null
) : BaseObservable() {

    @Transient
    @get:Bindable
    var selected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    fun cashOutTypeVal() =
        if ("3" == cashOutType) "银行卡提现" else if ("1" == cashOutType || "2" == cashOutType) "余额提现" else ""
}