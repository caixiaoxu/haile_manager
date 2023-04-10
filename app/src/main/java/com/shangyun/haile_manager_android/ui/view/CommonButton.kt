package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import com.lsy.framelib.ui.weight.SingleTapButton

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
class CommonButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SingleTapButton(context, attrs) {

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.15f
    }
}