package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.yunshang.haile_manager_android.R
import kotlin.math.max
import kotlin.math.min

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/27 09:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
open class BindingEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    val maxLength: Int

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.BindingEditText)
        maxLength = array.getInteger(R.styleable.BindingEditText_android_maxLength, -1)
        array.recycle()
    }

    override fun setText(txt: CharSequence?, type: BufferType?) {
        val temp = text
        val curStart = selectionStart
        super.setText(txt, type)
        if (temp != txt) {
            val len = (txt?.length ?: 0)
            setSelection(
                min(
                    if ((temp?.length ?: 0) == curStart || curStart >= len) len else curStart,
                    maxLength
                )
            )
        }
    }
}