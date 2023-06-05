package com.yunshang.haile_manager_android.ui.view.chart

import android.content.Context
import android.graphics.*
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.entities.HomeIncomeEntity
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.text.ParseException
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author 高德智
 * @project CampusWashingBusiness
 * @date 2023-03-18
 */
class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val ARROW_SIZE = 24f// 箭头的大小
    private val RADIUS_RADIUS = 16f//圆角
    private val PADDING_START = 18f
    private val PADDING_END = 18f

    private val tvTrendDate: TextView = findViewById(R.id.tv_trend_date)
    private val tvTrendAmount: TextView = findViewById(R.id.tv_trend_amount)

    private var listBean: List<HomeIncomeEntity>? = null
    var curBean: HomeIncomeEntity? = null

    private var bgColor = Color.parseColor("#F0A258")

    fun addData(listBean: List<HomeIncomeEntity>?) {
        this.listBean = listBean
    }

    // 设置标记视图的显示内容
    override fun refreshContent(e: Entry, highlight: Highlight) {
        curBean = listBean!![e.x.toInt() - 1]
        var data = ""
        try {
            data = DateTimeUtils.formatDateTimeForStr(
                curBean?.date,
                "MM-dd",
            )
        } catch (parseException: ParseException) {
            parseException.printStackTrace()
        }
        tvTrendDate.text = "$data ${curBean?.dateWeek}"
        val amount: Double
        try {
            amount = curBean?.amount ?: 0.0
            bgColor = if (amount < 0) {
                Color.parseColor("#30C19A")
            } else {
                Color.parseColor("#F0A258")
            }
        } catch (exception: NumberFormatException) {
            bgColor = Color.parseColor("#F0A258")
            exception.printStackTrace()
        }
        tvTrendAmount.text = "¥${curBean?.amount}"
        tvTrendAmount.compoundDrawablePadding = 8
        tvTrendAmount.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            getContext().getDrawable(R.mipmap.icon_triangle_right),
            null
        )
    }

    // 根据选中的条目的位置，调整标记视图的位置
    override fun getOffset(): MPPointF = MPPointF(-(width / 2f), -height.toFloat())

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val offset: MPPointF = offset
        val chart: Chart<*> = chartView
        val width: Float = width.toFloat()
        // posY \posX 指的是markerView左上角点在图表上面的位置
        //处理X方向，分为3种情况，1、在图表左边 2、在图表中间 3、在图表右边
        if (posX > chart.width - width) { //如果超过右边界，则向左偏移markerView的宽度
            offset.x = -(width - (chart.width - posX) + PADDING_END)
        } else { //默认情况，不偏移（因为是点是在左上角）
            offset.x = -(posX - PADDING_START)
            if (posX > width / 2) { //如果大于markerView的一半，说明箭头在中间，所以向右偏移一半宽度
                offset.x = -(width / 2)
            }
        }
        return offset
    }

    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        val whitePaint = Paint() //绘制底色白色的画笔
        whitePaint.style = Paint.Style.FILL
        whitePaint.color = bgColor
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()
        val offset: MPPointF = getOffsetForDrawingAtPoint(posX, posY)
        val saveId = canvas.save()

        // 圆角背景
        var path = Path()
        val rectF = RectF(0f, 0f, width, height)
        path.addRoundRect(
            rectF,
            RADIUS_RADIUS.toFloat(),
            RADIUS_RADIUS.toFloat(),
            Path.Direction.CW
        )
        path.offset(posX + offset.x, 0f)
        canvas.drawPath(path, whitePaint)
        // 倒三角
        path = Path()
        val sqrt = sqrt(ARROW_SIZE.toDouble().pow(2.0) - (ARROW_SIZE / 2f).toDouble().pow(2.0))
        path.moveTo(posX, height + java.lang.Double.valueOf(sqrt).toFloat())
        path.lineTo(posX - ARROW_SIZE / 2f, height)
        path.lineTo(posX + ARROW_SIZE / 2f, height)
        path.close()
        canvas.drawPath(path, whitePaint)
        // 画线
        whitePaint.strokeWidth = 2f
        canvas.drawLine(posX, posY, posX, ARROW_SIZE.toFloat(), whitePaint)
        // 移动画布
        canvas.translate(posX + offset.x, 0f)
        draw(canvas)
        canvas.restoreToCount(saveId)
    }
}