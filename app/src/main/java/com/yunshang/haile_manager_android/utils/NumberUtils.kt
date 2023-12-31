package com.yunshang.haile_manager_android.utils

import com.lsy.framelib.utils.StringUtils
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Title : 数字处理工具类
 * Author: Lsy
 * Date: 2023/4/10 14:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object NumberUtils {

    /**
     * 保留两位小数
     */
    @JvmStatic
    fun keepTwoDecimals(num: Double?): String = num?.let {
        BigDecimal(num).run { setScale(2, RoundingMode.HALF_UP).toString() }
    } ?: ""

    /**
     * 小数点后两位
     */
    @JvmStatic
    fun formatTwoDecimals(num: Float?): String = num?.let { StringUtils.format("%.2f", num) } ?: ""
}