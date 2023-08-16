package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

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
class BindingEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    override fun setText(txt: CharSequence?, type: BufferType?) {
        val temp = text
        super.setText(txt, type)
        if (temp != txt){
            setSelection(txt?.length ?: 0)
        }
    }
}