package com.yunshang.haile_manager_android.utils.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.text.style.ReplacementSpan


/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 18:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class VerticalBottomSpan(private val mFontSizePx: Float, private val mOffset: Float) :
    ReplacementSpan() {

    private fun getCustomTextPaint(srcPaint: Paint): TextPaint = TextPaint(srcPaint).apply {
        textSize = mFontSizePx //设定字体大小, sp转换为px
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int = getCustomTextPaint(paint).measureText(text?.substring(start, end)).toInt()

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // 此处重新计算y坐标，使字体居中
        text?.substring(start, end)
            ?.let { canvas.drawText(it, x, y + mOffset, getCustomTextPaint(paint)) }
    }
}