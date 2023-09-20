package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/20 09:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CustomFlowListConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val defaultSpace = DimensionUtils.dip2px(context, 8f)

    private val flowView = Flow(context)

    init {
        addView(flowView, 0)

        val array = context.obtainStyledAttributes(attrs, R.styleable.CustomFlowListConstraintLayout)
        flowView.setHorizontalBias(array.getFloat(R.styleable.CustomFlowListConstraintLayout_flow_horizontalBias, 0f))
        flowView.setHorizontalGap(array.getDimensionPixelSize(R.styleable.CustomFlowListConstraintLayout_flow_horizontalGap, defaultSpace))
        flowView.setHorizontalStyle(array.getInt(R.styleable.CustomFlowListConstraintLayout_flow_horizontalStyle, Flow.CHAIN_PACKED))
        flowView.setVerticalGap(array.getDimensionPixelSize(R.styleable.CustomFlowListConstraintLayout_flow_verticalGap, defaultSpace))
        flowView.setWrapMode(array.getInt(R.styleable.CustomFlowListConstraintLayout_flow_wrapMode, Flow.WRAP_CHAIN))
        array.recycle()
    }

    /**
     * 构建子布局
     */
    fun <B : ViewDataBinding, D> buildChild(
        itemList: List<D>?,
        layoutId: Int?,
        layoutParams: LinearLayoutCompat.LayoutParams? = null,
        start: Int? = null,
        onChildBinding: (index: Int, childBinding: B, data: D) -> Unit
    ) {
        // 子View不能为空
        if (null == layoutId) {
            return
        }
        // 数据为空隐藏
        if (itemList.isNullOrEmpty()) {
            visibility = GONE
            return
        }

        // 保留最初的view，因为多个flow，数量+1
        val saveNum = (start ?: 0) + 1
        if (childCount > saveNum) {
            removeViews(saveNum, childCount - saveNum)
        }
        // 循环加载
        val inflater = LayoutInflater.from(context)
        itemList.forEachIndexed { index, any ->
            val itemBinding = DataBindingUtil.inflate<B>(inflater, layoutId, null, false)
            onChildBinding.invoke(index, itemBinding, any)
            itemBinding.root.id = index + 1
            addView(
                itemBinding.root,
                layoutParams ?: LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
            )
        }
        // 设置id
        val idList = IntArray(itemList.size) { it + 1 }
        flowView.referencedIds = idList
        visibility = VISIBLE
    }
}