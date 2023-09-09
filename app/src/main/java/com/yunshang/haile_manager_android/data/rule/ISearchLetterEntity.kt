package com.yunshang.haile_manager_android.data.rule

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/8 10:01
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class ISearchLetterEntity {
    abstract fun getTitle(): String
    open fun getIcon(): String? = null
    open fun getLetter(): String? = null
    open var showLetter: Boolean = false
}