package com.shangyun.haile_manager_android.utils

import android.content.Context
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import com.lsy.framelib.utils.DimensionUtils
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
    @JvmStatic
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
    @JvmStatic
    fun createPriceStr(context: Context, price: Double): SpannableString =
        SpannableString("¥${NumberUtils.keepTwoDecimals(price)}").apply {
            setSpan(
                AbsoluteSizeSpan(DimensionUtils.sp2px(context, 24f)),
                0,
                1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )

        }

    /**
     * 格式化手机
     */
    @JvmStatic
    fun formatPhone(phone: String): String = phone.replaceRange(IntRange(3, 7), "****")

    /**
     * 格式化多样式字符串
     * @param content 内容
     * @param spans 多样式
     * @param start 开始位置
     * @param end 结束位置
     */
    fun formatMultiStyleStr(
        content: String,
        spans: Array<ParcelableSpan>,
        start: Int,
        end: Int
    ): SpannableString = SpannableString(content).apply {
        spans.forEach {
            setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }
}