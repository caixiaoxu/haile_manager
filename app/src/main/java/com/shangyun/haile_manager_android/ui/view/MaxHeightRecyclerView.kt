package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import com.shangyun.haile_manager_android.R

/**
 * Title : 含最大高度的ScrollView
 * Author: Lsy
 * Date: 2023/4/17 17:30
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MaxHeightRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    var maxHeight: Int = -1

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView)
        maxHeight = array.getDimensionPixelOffset(R.styleable.MaxHeightRecyclerView_maxHeight, -1)
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (0 < maxHeight) {
            if (measuredHeight > 0 && measuredHeight > maxHeight) {
                setMeasuredDimension(measuredWidth, maxHeight)
            }
        }
    }
}