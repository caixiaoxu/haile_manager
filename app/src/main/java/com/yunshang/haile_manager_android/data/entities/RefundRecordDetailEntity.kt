package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.extend.formatMoney

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/19 14:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class RefundRecordDetailEntity(
    val account: String,
    val createTime: String,
    val id: Int,
    val refundNo: String,
    val refundPrice: Double,
    val remark: String,
    val state: Int,
    val stateDesc: String,
    val transAccount: String,
    val transAccountType: Int,
    val transRealName: String,
    val updateTime: String,
    val userId: Int,
    val userTokenCoinRefundItemRecordVOList: List<UserTokenCoinRefundItemRecordVO>
) {
    fun isShowDesc(): Boolean = (3 == state || 2 == state) && remark.isNotEmpty()

    fun accountTitle(): String =
        (if (1 == transAccountType) StringUtils.getString(R.string.alipay_accout) else StringUtils.getString(
            R.string.wechat_accout
        )) + "："
}

data class UserTokenCoinRefundItemRecordVO(
    val presentAmount: Int,
    val principalAmount: Int,
    val refundPrice: Double,
    val shopId: Int,
    val shopName: String
) {
    val refundPriceVal: String
        get() = refundPrice.formatMoney()
}