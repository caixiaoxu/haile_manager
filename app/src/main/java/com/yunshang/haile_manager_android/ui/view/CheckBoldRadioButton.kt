package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/20 16:09
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CheckBoldRadioButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatRadioButton(context, attrs) {

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        typeface = if (checked) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }
}