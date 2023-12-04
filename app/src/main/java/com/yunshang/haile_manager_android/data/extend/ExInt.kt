package com.yunshang.haile_manager_android.data.extend

import java.text.DecimalFormat

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/16 10:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
fun Int?.hasVal(): Boolean = null != this && -1 != this

fun Int?.isGreaterThan0(): Boolean = null != this && 0 < this

fun Int?.formatMoney(symbol: Boolean = false): String = this?.let {
    (if (symbol && this > 0) "+" else "") + try {
        DecimalFormat("#,###").format(this)
    } catch (e: Exception) {
        ""
    }
} ?: ""