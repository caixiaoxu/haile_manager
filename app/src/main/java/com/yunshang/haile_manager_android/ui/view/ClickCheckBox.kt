package com.yunshang.haile_life.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/23 12:01
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ClickCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatCheckBox(context, attrs) {

    private var onCheckClickListener: ((v: View) -> Boolean)? = null
    fun setOnCheckClickListener(onCheckClickListener: (v: View) -> Boolean) {
        this.onCheckClickListener = onCheckClickListener
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (true == onCheckClickListener?.invoke(this)) {
                return true
            }
        }
        return super.onTouchEvent(ev)
    }
}