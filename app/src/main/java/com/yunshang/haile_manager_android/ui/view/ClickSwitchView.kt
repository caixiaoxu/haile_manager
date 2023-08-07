package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.SwitchCompat

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/8 17:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
class ClickSwitchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SwitchCompat(context, attrs) {

    private var onSwitchClickListener: ((v: SwitchCompat) -> Boolean)? = null
    fun setOnSwitchClickListener(onSwitchClickListener: (v: SwitchCompat) -> Boolean) {
        this.onSwitchClickListener = onSwitchClickListener
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (true == onSwitchClickListener?.invoke(this)) {
                return true
            }
        }
        return super.onTouchEvent(ev)
    }
}