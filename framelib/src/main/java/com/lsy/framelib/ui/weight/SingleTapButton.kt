package com.lsy.framelib.ui.weight

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.lsy.framelib.utils.ViewUtils

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
open class SingleTapButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

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