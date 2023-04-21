package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.shangyun.haile_manager_android.R

/**
 * Title : 适用于代码添加入自定义布局的LinearLayout
 * Author: Lsy
 * Date: 2023/4/21 13:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CustomChildListLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    var layoutId: Int = -1//child布局
    var space: Int = 0// 间距

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CustomChildListLinearLayout)
        layoutId = array.getResourceId(R.styleable.CustomChildListLinearLayout_itemLayout, -1)
        array.recycle()
    }

    /**
     * 构建子布局
     */
    fun <B : ViewDataBinding, D> buildChild(
        itemList: List<D>?,
        onChildBinding: (index: Int, childBinding: B, data: D) -> Unit
    ) {
        if (-1 == layoutId) {
            return
        }
        if (itemList.isNullOrEmpty()) {
            visibility = GONE
            return
        }
        visibility = VISIBLE

        removeAllViews()
        itemList.forEachIndexed { index, any ->
            val mBinding = DataBindingUtil.bind<B>(
                LayoutInflater.from(context).inflate(layoutId, null, false)
            )
            mBinding?.let {
                onChildBinding.invoke(index, it, any)
                addView(
                    it.root,
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                        if (0 < index)
                            setMargins(0, space, 0, 0)
                    })
            }
        }
    }
}