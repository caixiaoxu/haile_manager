package com.shangyun.haile_manager_android.utils

import android.content.Context
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import com.lsy.framelib.utils.DimensionUtils
import java.io.UnsupportedEncodingException
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

    /**
     * 格式化地区
     */
    fun formatArea(provinceName: String?, cityName: String?, districtName: String?): String =
        if (null == provinceName || null == cityName || null == districtName) "" else String.format(
            "%s-%s-%s",
            provinceName,
            cityName,
            districtName
        )

    // 获取一个汉字的首字母
    fun getFirstLetter(ch: Char): Char? {
        // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
        if (ch.code.shr(7) == 0) {
            return null
        }
        return try {
            var uniCode: ByteArray = ch.toString().toByteArray(charset("GBK"))
            if (uniCode[0] in 1..127) { // 非汉字
                null
            } else {
                convert(uniCode)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }


    private const val GB_SP_DIFF = 160

    // 存放国标一级汉字不同读音的起始区位码
    private val secPosValueList = intArrayOf(
        1601, 1637, 1833, 2078, 2274, 2302,
        2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
        4086, 4390, 4558, 4684, 4925, 5249, 5600
    )

    // 存放国标一级汉字不同读音的起始区位码对应读音
    private val firstLetter = charArrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
        'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
        'y', 'z'
    )

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    private fun convert(bytes: ByteArray): Char {
        var result = '-'
        var i = 0
        while (i < bytes.size) {
            bytes[i] = (bytes[i].toInt() - GB_SP_DIFF).toByte()
            i++
        }
        val secPosValue: Int = bytes[0] * 100 + bytes[1]
        i = 0
        while (i < 23) {
            if (secPosValue >= secPosValueList[i] && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i]
                break
            }
            i++
        }
        return result
    }
}