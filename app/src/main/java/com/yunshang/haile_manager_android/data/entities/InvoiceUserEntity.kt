package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.ICommonNewBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/19 10:54
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class InvoiceUserEntity(
    val id: Int? = null,
    val realName: String? = null
) : ICommonNewBottomItemEntity() {
}