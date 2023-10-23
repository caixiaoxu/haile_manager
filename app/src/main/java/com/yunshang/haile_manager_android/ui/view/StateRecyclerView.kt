package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.ui.view.adapter.BaseRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/12 17:17
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class StateRecyclerView<D> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val mRecyclerView = RecyclerView(context).apply {
        visibility = View.GONE
    }
    private val mStateTextView = AppCompatTextView(context).apply {
        textSize = 14f
        setTextColor(ContextCompat.getColor(context, R.color.common_sub_txt_color))
        gravity = Gravity.CENTER
        visibility = View.GONE
    }

    // 排版布局
    var layoutManager: RecyclerView.LayoutManager? = null
        set(value) {
            mRecyclerView.layoutManager = value
            field = value
        }

    // 适配器
    var adapter: BaseRecyclerAdapter<D>? = null
        set(value) {
            mRecyclerView.adapter = value
            field = value
        }

    /**
     * 分割线
     */
    fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        mRecyclerView.addItemDecoration(decor)
    }

    init {
        addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(
            mStateTextView,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, DimensionUtils.dip2px(context, 150f), 0, 0)
                gravity = Gravity.CENTER_HORIZONTAL
            })

        val array = context.obtainStyledAttributes(attrs, R.styleable.StateRecyclerView)
        setStateImg(
            array.getResourceId(
                R.styleable.StateRecyclerView_stateImg,
                R.mipmap.icon_list_content_empty
            )
        )
        setStateInfo(
            array.getString(R.styleable.StateRecyclerView_stateInfo)
                ?: StringUtils.getString(R.string.empty_content)
        )
        array.recycle()
    }

    fun setStateImg(drawTop: Int) {
        mStateTextView.setCompoundDrawablesWithIntrinsicBounds(0, drawTop, 0, 0)
    }

    fun setStateInfo(txt: String) {
        mStateTextView.text = txt
    }

    fun refreshData(list: MutableList<out D>?, isRefresh: Boolean = false) {
        // 刷新数据
        adapter?.refreshList(list, isRefresh)
        (isRefresh && list.isNullOrEmpty()).let {
            mRecyclerView.visibility(!it)
            mStateTextView.visibility(it)
        }
    }
}