package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.min

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/23 10:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MultiTypeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    // 背景
    var bgResIds: IntArray = intArrayOf()
        set(value) {
            field = value

            refreshType()
        }

    // 字体颜色
    var txtColors: IntArray = intArrayOf()
        set(value) {
            field = value

            refreshType()
        }

    // 类型
    var type: Int = 0
        set(value) {
            field = value

            refreshType()
        }

    /**
     * 刷新type样式
     */
    private fun refreshType() {
        if (bgResIds.isEmpty() || txtColors.isEmpty()) {
            return
        }
        //取出对应值
        val bgRes = bgResIds[min(type, bgResIds.size)]
        val txtColor = txtColors[min(type, bgResIds.size)]
        // 赋值
        setBackgroundResource(bgRes)
        setTextColor(txtColor)
    }
}