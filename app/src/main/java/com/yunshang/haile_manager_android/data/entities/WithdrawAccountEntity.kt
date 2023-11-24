package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/12 16:38
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class WithdrawAccountEntity(
    val bank: String? = null,
    val cashOutAccount: String? = null,
    val cashOutRate: Int? = null,
    val cashOutType: Int? = null,
    val exist: Boolean? = null,
    val icon: String? = null,
    val id: Int? = null,
    val maxWithdrawAmount: String? = null,
    val minWithdrawAmount: String? = null,
    val realName: String? = null,
    val state: Int? = null
)