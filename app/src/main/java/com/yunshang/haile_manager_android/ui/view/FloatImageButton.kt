package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton
import com.lsy.framelib.utils.ScreenUtils
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/12 10:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class FloatImageButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {

    private var lastX: Int = 0
    private var lastY: Int = 0
    private var mDownX: Int = 0
    private var mDownY: Int = 0

    //ViewTree的宽和高
    private val mScreenWidth: Int = ScreenUtils.screenWidth
    private val mScreenHeight: Int = ScreenUtils.screenHeight

    // 重写onTouchEvent方法，主要在这个方法里面来处理点击和拖动事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eX = event.rawX.toInt()
        val eY = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = eX
                lastY = eY
                mDownX = lastX// 判断是点击事件还是拖动事件时需要用到
                mDownY = lastY
            }
            MotionEvent.ACTION_UP -> {
                if (calculateDistance(mDownX, mDownY, eX, eY) <= 5) {
                } else {
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = eX - lastX
                val dy = eY - lastY
                // 计算组件此时的位置，距离父容器上下左右的距离=偏移量 + 原来的距离
                var left = left + dx
                var top = top + dy
                var right = right + dx
                var bottom = bottom + dy
                // 更新最后位置
                lastX = eX
                lastY = eY

                if (top < 0) {
                    // 移出了上边界
                    top = 0
                    bottom = height
                }
                if (left < 0) {
                    // 移出了左边界
                    left = 0
                    right = width
                }
                if (bottom > mScreenHeight) {
                    // 移出了下边界
                    bottom = mScreenHeight
                    top = bottom - height
                }
                if (right > mScreenWidth) {
                    // 移出了右边界
                    right = mScreenWidth
                    left = right - width
                }
                layout(left, top, right, bottom)
            }
        }

        return super.onTouchEvent(event)
    }

    private fun calculateDistance(downX: Int, downY: Int, lastX: Int, lastY: Int): Float {
        return sqrt(((lastX - downX) * 1.0f).pow(2) + ((lastY - downY) * 1.0f).pow(2))
    }
}