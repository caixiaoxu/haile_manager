package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
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
        ViewUtils.inputAmountLimit(this)
        inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        maxLines = 1
    }
}