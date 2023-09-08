package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.ISearchLetterEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/7 18:52
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SubBankEntity(
    val subBankCode: String,
    val subBankName: String
) : ISearchLetterEntity() {
    override fun getTitle(): String = subBankName

    override fun getIcon(): String? = null

    override fun getLetter(): String? = null
}