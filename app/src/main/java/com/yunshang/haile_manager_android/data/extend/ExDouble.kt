package com.yunshang.haile_manager_android.data.extend

import java.math.BigDecimal
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
fun Double?.hasVal(): Boolean = null != this && 0 < this

fun Double?.formatMoney(symbol: Boolean = false): String = this?.let {
    (if (symbol && this > 0) "+" else "") + try {
        DecimalFormat(",##0.00").format(BigDecimal(this))
    } catch (e: Exception) {
        ""
    }
} ?: ""

fun Double?.formatNumber(symbol: Boolean = false): String = this?.let {
    (if (symbol && this > 0) "+" else "") + try {
        DecimalFormat("##0.00").format(BigDecimal(this))
    } catch (e: Exception) {
        ""
    }
} ?: ""

