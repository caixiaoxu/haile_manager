package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children
import com.lsy.framelib.utils.DimensionUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/24 15:51
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DownTriangleBackgroundLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#45484A")
        }
    }

    val dimen8 = DimensionUtils.dip2px(context, 8f).toFloat()
    val dimen14 = DimensionUtils.dip2px(context, 14f).toFloat()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var childHeight = 0
        children.forEach {
            childHeight += it.measuredHeight
        }

        // 加上三角高度
        setMeasuredDimension(
            widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                (childHeight + dimen8).toInt(),
                MeasureSpec.EXACTLY
            )
        )
    }

    override fun dispatchDraw(canvas: Canvas?) {
        val path = Path()

        val rectBottom = measuredHeight - dimen8
        // 三角
        path.reset()
        path.moveTo(dimen14, rectBottom - 1)
        path.lineTo(dimen14 + dimen8, measuredHeight.toFloat())
        path.lineTo(dimen14 + dimen8 * 2, rectBottom - 1)
        canvas?.drawPath(path, mPaint)
        // 圆角矩形
        canvas?.drawRoundRect(
            0f,
            0f,
            measuredWidth.toFloat(),
            rectBottom,
            dimen8,
            dimen8,
            mPaint
        )
        canvas?.save()
        canvas?.clipRect(0f, 0f, measuredWidth.toFloat(), rectBottom)
        super.dispatchDraw(canvas)
        canvas?.restore()
    }
}