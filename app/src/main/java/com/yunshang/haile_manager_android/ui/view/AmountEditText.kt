package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.utils.ViewUtils

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
class AmountEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.AmountEditText)
        val maxInputLen = array.getColor(R.styleable.AmountEditText_maxInputLen, 4)
        val maxPointLen = array.getColor(R.styleable.AmountEditText_maxPointLen, 2)
        array.recycle()
        ViewUtils.inputAmountLimit(this, maxInputLen, maxPointLen)
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        maxLines = 1
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        setSelection(text?.length ?: 0)
    }
}