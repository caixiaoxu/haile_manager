package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/23 10:10
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RingChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ringWidth = DimensionUtils.dip2px(context, 24f).toFloat()

    private val mPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = ringWidth
        }
    }

    var colors = listOf(
        ContextCompat.getColor(context, R.color.colorPrimary),
        ContextCompat.getColor(context, R.color.color_pink),
        ContextCompat.getColor(context, R.color.color_green)
    )

    var data: List<Float>? = null
        set(value) {
            field = value
            invalidate()
        }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val rectF =
            RectF(ringWidth / 2, ringWidth / 2, width - ringWidth / 2, height - ringWidth / 2)
        data?.let {
            val size = it.filter { item -> item > 0f }.size
            var start = -90f
            if (0 == size) {
                mPaint.color = colors[0]
                canvas?.drawArc(rectF, 0f, 360f, false, mPaint)
            } else {
                it.forEachIndexed { index, percent ->
                    if (percent > 0f) {
                        mPaint.color = colors[index % it.size]
                        val sweepAngle = (360 * percent)
//                Timber.i("开始角度：$start,孤度:$sweepAngle")
                        canvas?.drawArc(
                            rectF,
                            start,
                            sweepAngle - (if (size > 1) 1 else 0),
                            false,
                            mPaint
                        )
                        start += sweepAngle
                    }
                }
            }
        } ?: run {
            mPaint.color = colors[0]
            canvas?.drawArc(rectF, 0f, 360f, false, mPaint)
        }
    }
}