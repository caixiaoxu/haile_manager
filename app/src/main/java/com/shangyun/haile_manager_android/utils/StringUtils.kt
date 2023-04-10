package com.shangyun.haile_manager_android.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.R
import java.util.regex.Pattern

/**
 * Title : 字符串工具类
 * Author: Lsy
 * Date: 2023/4/4 18:12
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object StringUtils {

    /**
     * 验证密码
     * 6-12位包含大小写字母和数字三种组合
     *
     * @param password
     * @return
     */
    fun checkPassword(password: String?): Boolean {
        var flag: Boolean = try {
            val check = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z\\d]{6,12}$"
            val regex = Pattern.compile(check)
            val matcher = regex.matcher(password)
            matcher.matches()
        } catch (e: Exception) {
            false
        }
        return flag
    }

    /**
     * 创建金额字符串
     */
    fun createPriceStr(context: Context, price: Double): SpannableString =
        SpannableString("¥${NumberUtils.keepTwoDecimals(price)}").apply {
            setSpan(
                AbsoluteSizeSpan(DimensionUtils.sp2px(context, 24f)),
                0,
                1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )

        }
}