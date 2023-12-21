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
data class BankEntity(
    val bankId: Int,
    val bankCode: String,
    val bankName: String,
    val firstLetter: String,
    val url: String
) : ISearchLetterEntity() {

    override fun getTitle(): String = bankName

    override fun getIcon(): String = url

    override fun getLetter(): String = firstLetter
}