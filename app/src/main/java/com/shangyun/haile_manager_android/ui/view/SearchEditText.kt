package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/24 14:08
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class SearchEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    var onTextChange: (() -> Unit)? = null

    private val mHandler = Handler(Looper.getMainLooper()) {
        onTextChange?.invoke()
        false
    }

    init {
        maxLines = 1
        isSingleLine = true
        imeOptions = EditorInfo.IME_ACTION_SEARCH

        addTextChangedListener {
            mHandler.removeMessages(0)
            mHandler.sendEmptyMessageDelayed(0, 600)
        }

        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mHandler.removeMessages(0)
                onTextChange?.invoke()
                true
            } else false
        }
    }
}