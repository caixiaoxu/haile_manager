package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.lsy.framelib.utils.ViewUtils
import com.shangyun.haile_manager_android.R

/**
 * Title : 常用按钮
 * Author: Lsy
 * Date: 2023/4/3 16:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CommonBtn @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.15f
    }

    override fun setOnClickListener(l: OnClickListener?) {
        // 防重复点击处理
        val onclick = OnClickListener { view ->
            if (!ViewUtils.isFastDoubleClick()) {
                l?.onClick(view)
            }
        }
        super.setOnClickListener(onclick)
    }
}